package org.ideaedu.loadtest

import grails.plugins.rest.client.RestBuilder
import groovyx.gpars.GParsPool

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
	def static REPORT_MODEL_THREADS = 10
	def static QUESTIONS_MODEL_THREADS = 10
	
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
			render template: 'error', model: [status: status]
			return
		}
		
		def json = response.json
		def reportCount = json.data.size()
		def duration = end - start
		def rate = reportCount*1000L*3600L/duration
		
		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, test: 'reports']
	}
	
	def getSurveyIds()
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
				json = response.json
				pageResults = json.data.collect {it.survey_id}
				
				if (pageResults) surveyIds += pageResults
				page++
			}
			catch(e)
			{
				status = 'Connection timed out'
			}
		}
		
		//println 'Exiting getSurveyIds()'
		
		return surveyIds
	}
	
	def loadTestExistingReportModels()
	{
		def url = getBaseUrl(params.host, params.port) + '/v1/reports'
		
		def surveyIds = getSurveyIds()
		println "Found ${surveyIds.size()} unique survey IDs"
		
		println "Starting timer..."
		def start = System.currentTimeMillis()
		
		def response
		def status
		
		def json
		def reportIds = [] as Set
		
		GParsPool.withPool SURVEY_REPORTS_THREADS, {
			surveyIds.eachParallel
			{
				response = restBuilder.get(url + '?survey_id=' + it + '&type=ALL') {
					header 'X-IDEA-APPNAME', app
					header 'X-IDEA-KEY', appKey
				}
				
				status = response.status
				json = response.json
				
				def ids = json.data.collect {it.id}
				reportIds += ids
			}
		}
		
		println "Found ${reportIds.size()} unique report IDs"
		
		GParsPool.withPool REPORT_MODEL_THREADS, {
			reportIds.eachParallel
			{
				response = restBuilder.get(url[0..-2] + '/' + it + '/model') {
					header 'X-IDEA-APPNAME', app
					header 'X-IDEA-KEY', appKey
				}
				
				status = response.status
				json = response.json
			}
		}
		
		def end = System.currentTimeMillis()
		println "Ending timer"
		def duration = end - start
		def reportCount = reportIds.size()
		def rate = reportCount*1000L*3600L/duration
		println duration
		println rate
		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, test: 'reportModels']
	}
	
	def loadTestExistingReportModelsAndQuestions()
	{
		def url = getBaseUrl(params.host, params.port) + '/v1/reports'
		
		def surveyIds = getSurveyIds()
		println "Found ${surveyIds.size()} unique survey IDs"
		
		println "Starting timer..."
		def start = System.currentTimeMillis()
		
		def response
		def status
		
		def json
		def reportIds = [] as Set
		
		GParsPool.withPool SURVEY_REPORTS_THREADS, {
			surveyIds.eachParallel
			{
				response = restBuilder.get(url + '?survey_id=' + it + '&type=ALL') {
					header 'X-IDEA-APPNAME', app
					header 'X-IDEA-KEY', appKey
				}
				
				status = response.status
				json = response.json
				
				def ids = json.data.collect {it.id}
				reportIds += ids
			}
		}
		
		println "Found ${reportIds.size()} unique report IDs"
		
		def reportModelsAndQuestions = [:]
		
		GParsPool.withPool REPORT_MODEL_THREADS, {
			reportIds.eachParallel
			{
				response = restBuilder.get(url[0..-2] + '/' + it + '/model') {
					header 'X-IDEA-APPNAME', app
					header 'X-IDEA-KEY', appKey
				}
				
				status = response.status
				reportModelsAndQuestions.putAt(it.toString(), response.json)
			}
		}
		
		def reportsWithNoData = 0
		
		GParsPool.withPool REPORT_MODEL_THREADS, {
			reportModelsAndQuestions.eachParallel
			{
				keyValuePair ->
				def dataPoints = keyValuePair?.value?.aggregate_data
				
				if (!dataPoints) reportsWithNoData++
				else
				{
					GParsPool.withPool QUESTIONS_MODEL_THREADS, {
						dataPoints.response_data_points.eachParallel
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
							}
							
							json = response.json
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
		println duration
		println rate
		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, reportsWithNoData: reportsWithNoData, test: 'reportModelQuestions']
	}
}