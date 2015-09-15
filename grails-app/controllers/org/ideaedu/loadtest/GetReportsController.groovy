package org.ideaedu.loadtest

import grails.plugins.rest.client.RestBuilder
import groovyx.gpars.GParsPool
import java.util.concurrent.ConcurrentHashMap

/**
 * This controller will perform load tests on the IDEA-REST-SERVER GET ../reports... end-point
 * @author daniel
 *
 */
class GetReportsController
{
	def restBuilder = new RestBuilder()
	def app = 'IOL3'
	def appKey = '872ttyu8d47a07c6330430lkq39500c5072bp822'
	def jsonContent = 'application/json;charset=utf-8'
	
	def static SURVEY_REPORTS_THREADS = 1
	def static REPORT_MODEL_THREADS = 1
	def static QUESTIONS_MODEL_THREADS = 1
	
	def getBaseUrl(host, port)
	{
		if (!host) host = 'localhost'
		if (!port) port = 8091
		
		return "http://${host}:${port}/IDEA-REST-SERVER"
	}
	
	def getUrlForPage(url, page)
	{
		if (!page) page=0
		def suffix = url.indexOf('?') != -1 ? "&page=${page}" : "?page=${page}"
		return "${url}${suffix}"
	}
	
	def loadTestExistingReports()
	{
		def url = getBaseUrl(params.host, params.port) + '/v1/reports'
		
		def start = System.currentTimeMillis()
		
		def response
		def status
		
		try
		{
			response = restBuilder.get(url) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
			}
			status = response.status
		}
		catch(e)
		{
			status = 'Connection timed out'
		}
		
		def end = System.currentTimeMillis()
		
		if (status != 200)
		{
			render template: 'error', model: [status: status, test: 'reports']
			return
		}
		
		def json = response.json
		def reportCount = json.data.size()
		def duration = end - start
		def rate = reportCount*1000L*3600L/duration
		
		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, test: 'reports']
	}
	
	def getSurveyIds(test)
	{
		println 'Looking for survey IDs...'
		
		def page = 0
		def surveyIds = [] as Set
		def pageResults
		def response
		def status
		def json
		
		while (pageResults != [])
		{
			def url = getUrlForPage(getBaseUrl(params.host, params.port) + "/v1/reports", page)
			
			try
			{
				response = restBuilder.get(url) {
					header 'X-IDEA-APPNAME', app
					header 'X-IDEA-KEY', appKey
				}
				
				status = response.status
				
				if (status != 200)
				{
					render template: 'error', model: [status: status, test: test]
					return
				}
				
				json = response.json
				pageResults = json.data.collect {it.survey_id}
				
				if (pageResults) surveyIds += pageResults
				page++
			}
			catch(e)
			{
				status = 'Connection timed out'
				println status
				break
			}
		}
		
		return surveyIds
	}
	
	def loadTestExistingReportModels()
	{
		println params
		def url = getBaseUrl(params.host, params.port) + '/v1/reports'
		
		def surveyIds = getSurveyIds('reportModels')
		
		if (!surveyIds)
		{
			render template: 'error', model: [status: 'Connection timed out', test: 'reportModels']
			return
		}
		
		println "Found ${surveyIds.size()} unique survey IDs"
		
		def rthreads = params.reportThreads ?: REPORT_MODEL_THREADS
		rthreads = rthreads as int
		if (rthreads < 1) rthreads = REPORT_MODEL_THREADS
		
		println "Utilizing ${rthreads} thread${rthreads > 1 ? 's' : ''} to retrieve the report models"
		
		println "Starting timer..."
		def start = System.currentTimeMillis()
		
		def response
		def status
		
		def json
		def reportIds = [] as Set
		
		// this should be done with 1 thread only, it is fast enough
		GParsPool.withPool SURVEY_REPORTS_THREADS, {
			surveyIds.eachParallel
			{
				synchronized(reportIds)
				{
					response = restBuilder.get(url + '?survey_id=' + it + '&type=ALL') {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
					}
					
					status = response.status
					
					if (status != 200)
					{
						render template: 'error', model: [status: status, test: 'reportModels']
						return
					}
					
					json = response.json
					
					reportIds += json.data.collect {it.id}
				}
			}
		}
		
		println "Found ${reportIds.size()} unique report IDs"
		
		GParsPool.withPool rthreads, {
			reportIds.eachParallel
			{
				response = restBuilder.get(url[0..-2] + '/' + it + '/model') {
					header 'X-IDEA-APPNAME', app
					header 'X-IDEA-KEY', appKey
				}
				
				status = response.status
				
				if (status != 200)
				{
					render template: 'error', model: [status: status, test: 'reportModels']
					return
				}
				
				json = response.json
				
				// not doing anything with the data yet; if we do, synchronization might be needed
			}
		}
		
		def end = System.currentTimeMillis()
		println "Ending timer"
		def duration = end - start
		def reportCount = reportIds.size()
		def rate = reportCount*1000L*3600L/duration

		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, test: 'reportModels']
	}
	
	def loadTestExistingReportModelsAndQuestions()
	{
		println params
		def url = getBaseUrl(params.host, params.port) + '/v1/reports'
		
		def surveyIds = getSurveyIds('reportModelQuestions')
		
		if (!surveyIds)
		{
			render template: 'error', model: [status: 'Connection timed out', test: 'reportModelQuestions']
			return
		}
		
		println "Found ${surveyIds.size()} unique survey IDs"
		
		def rthreads = params.reportThreads ?: REPORT_MODEL_THREADS
		def qthreads = params.questionThreads ?: QUESTIONS_MODEL_THREADS
		rthreads = rthreads as int
		qthreads = qthreads as int
		
		if (rthreads < 1) rthreads = REPORT_MODEL_THREADS
		if (qthreads < 1) qthreads = QUESTIONS_MODEL_THREADS
		
		println "Utilizing ${rthreads} thread${rthreads > 1 ? 's' : ''} to retrieve the report models and ${qthreads} thread${qthreads > 1 ? 's' : ''} to retrieve the question models"
		
		println "Starting timer..."
		def start = System.currentTimeMillis()
		
		def response
		def status
		
		def json
		def reportIds = [] as Set
		
		// this should stay at 1 so synchronization should not be necessary
		GParsPool.withPool SURVEY_REPORTS_THREADS, {
			surveyIds.eachParallel
			{
				synchronized(reportIds)
				{
					response = restBuilder.get(url + '?survey_id=' + it + '&type=ALL') {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
					}
					
					status = response.status
					
					if (status != 200)
					{
						render template: 'error', model: [status: status, test: 'reportModelQuestions']
						return
					}
					
					json = response.json.data as List
					
					reportIds += json.collect {it.id}
				}
			}
		}
		
		println "Found ${reportIds.size()} unique report IDs"
		
		def reportModelsAndQuestions = [:]
		
		GParsPool.withPool rthreads, {
			reportIds.eachParallel
			{
				synchronized(reportModelsAndQuestions)
				{
					response = restBuilder.get(url[0..-2] + '/' + it + '/model') {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
					}
					
					status = response.status
					
					if (status != 200)
					{
						render template: 'error', model: [status: status, test: 'reportModelQuestions']
						return
					}
					
					// key is report id, value is aggregate data
					reportModelsAndQuestions.putAt(it.toString(), response.json)
				}
			}
		}
		
		def reportsWithNoData = 0
		
		GParsPool.withPool rthreads, {
			reportModelsAndQuestions.eachParallel
			{
				keyValuePair ->
				def aggData = keyValuePair?.value?.aggregate_data
				
				//println "$keyValuePair.key : $keyValuePair.value"
				
				if (!aggData) reportsWithNoData++
				else
				{
					def dataPoints = Collections.synchronizedList(aggData.response_data_points)
					
					GParsPool.withPool qthreads, {
						dataPoints.eachParallel
						{
							dpoint ->
							def qUrl = url[0..-2] + '/' + keyValuePair.key + '/model/' + dpoint?.question_id
							
							try
							{
								response = restBuilder.get(qUrl) {
									header 'X-IDEA-APPNAME', app
									header 'X-IDEA-KEY', appKey
								}
								status = response.status
							}
							catch(e)
							{
								status = 'Connection timed out'
								render template: 'error', model: [status: status, test: 'reportModelQuestions']
								return
								
							}
							
							json = response.json
							// not doing anything with the data yet
						}
					}
				}
			}
		}
		
		def end = System.currentTimeMillis()
		println "Ending timer"
		def duration = end - start
		def reportCount = reportIds.size()
		def rate = reportCount*1000L*3600L/duration

		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, reportsWithNoData: reportsWithNoData, test: 'reportModelQuestions']
	}
}