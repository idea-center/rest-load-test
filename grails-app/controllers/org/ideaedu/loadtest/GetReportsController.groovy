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
	def jsonContent = 'application/json;charset=utf-8'
	
	// sent via params
	def app
	def appKey
	
	// number of threads to use when reading survey ids in order to get reports; keep at 1 for best results
	// the corresponding call is not included in rate measurements
	def static SURVEY_REPORTS_THREADS = 1
	
	// number of threads to use when calling the GET ../report/:id/model endpoint in parallel
	def static REPORT_MODEL_THREADS = 1
	
	// number of threads to use when calling the GET ../report/:id/model/:questionid endpoint in parallel
	def static QUESTIONS_MODEL_THREADS = 1
	
	// how many survey ids to read when testing GET report data (generally there are 2 reports per survey id)
	def static MAX_SURVEY_IDS_TO_GET = 100
	
	/**
	 * Builds the correct url to call
	 * @param host
	 * @param port
	 * @return
	 */
	def getBaseUrl(host, port)
	{
		if (!host) host = 'localhost'
		if (!port) port = 8091
		
		return "http://${host}:${port}/IDEA-REST-SERVER"
	}
	
	/**
	 * Adds page parameter to http call
	 * @param url
	 * @param page
	 * @return
	 */
	def getUrlForPage(url, page)
	{
		if (!page) page=0
		def suffix = url.indexOf('?') != -1 ? "&page=${page}" : "?page=${page}"
		return "${url}${suffix}"
	}
	
	/**
	 * Test the GET /reports endpoint
	 * @return
	 */
	def loadTestExistingReports()
	{
		log.info params
		def url = getBaseUrl(params.host, params.port) + '/v1/reports'
		
		app = params.appName
		appKey = params.appKey
		
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
		def totalReports = json.total_results
		log.info json
		log.info "Finished in ${duration/1000} seconds"
		
		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, totalReports: totalReports, test: 'reports']
	}
	
	/**
	 * Reads the survey ids available on the server. This is a helper method and it is NOT taken into account for rate measurements.
	 * @param test
	 * @param app
	 * @param appKey
	 * @return
	 */
	def getSurveyIds(test, app, appKey, maxSurveyIds)
	{
		log.info 'Looking for survey IDs...'
		
		def page = 0
		def surveyIds = [] as Set
		def pageResults
		def response
		def status
		def json
		
		/*def maxSurveyIds = params.reportCount ?: MAX_SURVEY_IDS_TO_GET
		maxSurveyIds = maxSurveyIds as int
		if (maxSurveyIds < 1) maxSurveyIds = MAX_SURVEY_IDS_TO_GET*/
		
		// read survey ids from the server until the maximum number (from params) is reached, or until there are no more surveys available (the loop passes increasing values for the page param)
		while (surveyIds.size() < maxSurveyIds && pageResults != [])
		{
			def url = getUrlForPage(getBaseUrl(params.host, params.port) + "/v1/reports?max=${maxSurveyIds}", page)
			
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
				break
			}
		}
		
		return surveyIds
	}
	
	/**
	 * Test the GET /report/:id/model endpoint. This test is mainly to ensure that reports are available. Only report metadata is returned.
	 * @return
	 */
	def loadTestExistingReportModels()
	{
		log.info params
		def url = getBaseUrl(params.host, params.port) + '/v1/reports'
		
		app = params.appName
		appKey = params.appKey
		
		def maxSurveyIds = params.reportCount ?: MAX_SURVEY_IDS_TO_GET
		maxSurveyIds = maxSurveyIds as int
		if (maxSurveyIds < 1) maxSurveyIds = MAX_SURVEY_IDS_TO_GET
		
		// get the collection of survey ids to use in getting reports
		def surveyIds = getSurveyIds('reportModels', app, appKey, maxSurveyIds)
		
		if (!surveyIds)
		{
			render template: 'error', model: [status: 'Connection timed out', test: 'reportModels']
			return
		}
		
		log.info "Found ${surveyIds.size()} unique survey IDs"
		
		def rthreads = params.reportThreads ?: REPORT_MODEL_THREADS
		rthreads = rthreads as int
		if (rthreads < 1) rthreads = REPORT_MODEL_THREADS
		
		log.info "Utilizing ${rthreads} thread${rthreads > 1 ? 's' : ''} to retrieve the report models"
		
		log.info "Starting timer..."
		def start = System.currentTimeMillis()
		
		def response
		def status
		
		def json
		def reportIds = [] as Set
		
		// this should be done with 1 thread only
		GParsPool.withPool SURVEY_REPORTS_THREADS, {
			surveyIds.eachParallel
			{
				// this block is synchronized so that the collection of report ids is not updated updated concurrently
				// data loss can occur if this block is not synchronized
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
		
		log.info "Found ${reportIds.size()} unique report IDs"
		
		// call the GET /report/:id/model endpoint in parallel
		// an alternative to GParsPool.withPool is GParsPool.withExecutorPool; former is more efficient, latter might be safer - to investigate
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
		log.info "Ending timer"
		def duration = end - start
		def reportCount = reportIds.size()
		def rate = reportCount*1000L*3600L/duration
		
		log.info "Finished in ${duration/1000} seconds"

		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, test: 'reportModels']
	}
	
	/**
	 * Test the GET /report/:id/model/:questionid endpoint
	 * @return
	 */
	def loadTestExistingReportModelsAndQuestions()
	{
		log.info params
		def url = getBaseUrl(params.host, params.port) + '/v1/reports'
		
		app = params.appName
		appKey = params.appKey
		
		def maxSurveyIds = params.reportCount ?: MAX_SURVEY_IDS_TO_GET
		maxSurveyIds = maxSurveyIds as int
		if (maxSurveyIds < 1) maxSurveyIds = MAX_SURVEY_IDS_TO_GET
		
		// get the collection of survey ids to use in getting reports
		def surveyIds = getSurveyIds('reportModelQuestions', app, appKey, maxSurveyIds)
		
		if (!surveyIds)
		{
			render template: 'error', model: [status: 'Connection timed out', test: 'reportModelQuestions']
			return
		}
		
		log.info "Found ${surveyIds.size()} unique survey IDs"
		
		def rthreads = params.reportThreads ?: REPORT_MODEL_THREADS
		def qthreads = params.questionThreads ?: QUESTIONS_MODEL_THREADS
		rthreads = rthreads as int
		qthreads = qthreads as int
		
		if (rthreads < 1) rthreads = REPORT_MODEL_THREADS
		if (qthreads < 1) qthreads = QUESTIONS_MODEL_THREADS
		
		log.info "Utilizing ${rthreads} thread${rthreads > 1 ? 's' : ''} to retrieve the report models and ${qthreads} thread${qthreads > 1 ? 's' : ''} to retrieve the question models"
		
		log.info "Starting timer..."
		def start = System.currentTimeMillis()
		
		def response
		def status
		
		def json
		def reportIds = [] as Set
		
		// this should stay at 1 so synchronization should not be necessary
		// represents the 1st query in the sequence used by CL
		GParsPool.withPool SURVEY_REPORTS_THREADS, 
		{
			surveyIds.eachParallel
			{
				// this block is synchronized so that collection of report ids is not updated updated concurrently
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
		
		log.info "Found ${reportIds.size()} unique report IDs"
		
		def reportModelsAndQuestions = [:]
		
		// call the GET /report/:id/model endpoint in parallel
		// represents the 2nd query in the sequence used by CL
		GParsPool.withPool rthreads, 
		{
			reportIds.eachParallel
			{
				// this block is synchronized so that the map of [report_id : aggregate_data] is not updated updated concurrently
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
					
					// key is report id, value is aggregate data that contains the question model endpoints 
					reportModelsAndQuestions.putAt(it.toString(), response.json)
				}
			}
		}
		
		def reportsWithNoData = 0
		
		// call the GET /report/:id/model/:questionid endpoint in parallel
		// represents the 3rd query in the sequence used by CL
		GParsPool.withPool rthreads, 
		{
			reportModelsAndQuestions.eachParallel
			{
				keyValuePair ->
				def aggData = keyValuePair?.value?.aggregate_data
				
				if (!aggData) reportsWithNoData++
				else
				{
					// a synchronized list seemed to work well here
					def dataPoints = Collections.synchronizedList(aggData.response_data_points)
					
					// nested parallel loop - investigate effects on server!
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
							}
							
							json = response.json
							// not doing anything with the data yet
						}
					}
				}
			}
		}
		
		def end = System.currentTimeMillis()
		log.info "Ending timer"
		def duration = end - start
		def reportCount = reportIds.size()
		def rate = reportCount*1000L*3600L/duration
		
		log.info "Finished in ${duration/1000} seconds"

		render template: 'loadTestExistingReports', model: [status: status, reportCount: reportCount, duration: duration, rate: (int)rate, reportsWithNoData: reportsWithNoData, test: 'reportModelQuestions']
	}
}