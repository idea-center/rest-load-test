package org.ideaedu.loadtest

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import groovyx.gpars.GParsPool

/**
 * This controller will perform load tests on the IDEA-REST-SERVER POST ../services/survey end-point, as well as GET surveys
 * @author daniel
 *
 */
class PostSurveysController
{
	def restBuilder = new RestBuilder()
	def jsonContent = 'application/json;charset=utf-8'
	
	// sent via params
	def app
	def appKey
	
	// generates survey data with random responses
	def surveyGeneratorService
	
	// number of threads to use when posting survey data
	def static SURVEY_REPORTS_THREADS = 1
	
	// default number of surveys to POST if not given via params
	def static SURVEY_COUNT = 1
	
	// using more than 5000 generated surveys is likely to cause out of memory errors
	def static MAX_SURVEYS_TO_POST = 5000

	/**
	 * Tests the POST surveys endpoint by sending generated json
	 * @return
	 */
	// TODO: allow posting more than 5000 surveys by using a for loop, generating 5000 survey objects in memory at a time, then clearing the collection before the next batch, etc.
	def loadTestPostSurveys()
	{
		log.info params
		def url = getBaseUrl(params.host, params.port) + '/v1/services/survey'
		log.info url
		
		app = params.appName
		appKey = params.appKey
		
		def surveyCount = params.surveyCount ?: SURVEY_COUNT
		surveyCount = surveyCount as int
		if (surveyCount < 1) surveyCount = SURVEY_COUNT
		if (surveyCount > MAX_SURVEYS_TO_POST) surveyCount = MAX_SURVEYS_TO_POST
		
		def sthreads = params.surveyThreads ?: SURVEY_REPORTS_THREADS
		sthreads = sthreads as int
		if (sthreads < 1) sthreads = SURVEY_REPORTS_THREADS
		
		def sampleSurveys = []
		
		for (i in 1..surveyCount)
		{
			// use faculty info form, diagnostic rater form, no extra questions
			sampleSurveys << surveyGeneratorService.buildRESTSurvey(1, 9, null, true)
		}
		
		def response
		def status
		
		// counts surveys successfully saved
		def savedSurveys = 0
		
		log.info "Using $sthreads thread(s) to post ${surveyCount} surveys"
		
		log.info "Starting timer..."
		def start = System.currentTimeMillis()
		
		// POST survey data in parallel by calling the endpoint via a thread pool
		GParsPool.withPool sthreads,
		{
			sampleSurveys.eachParallel
			{ 
				surveyData ->
				// synchronization is necessary, in its absence I was getting 2 to 4% errors in saving survey data (100 surveys, 10 threads)
				// I did not get errors with 5 threads or less. Also, performance was higher with 5 threads vs 10.
				synchronized(sampleSurveys)
				{
					try
					{
						response = restBuilder.post(url)
						{
							header 'X-IDEA-APPNAME', app
							header 'X-IDEA-KEY', appKey
							header 'Content-Type', jsonContent
							header 'Connection', 'keep-alive'
							json 	surveyData.toJSON()
						}
	
						status = response.status
	
						if (status == 200) savedSurveys++
					}
					catch(e)
					{
						status = 'Connection timed out'
					}
				}
			}
		}

		def end = System.currentTimeMillis()
		log.info "Ending timer"
		
		/*if (status != 200)
		{
			if (params.isAngular)
			{
				def resp = [status: status, test: 'postSurveys']
				render resp as JSON
			}
			else render template: 'error', model: [status: status, test: 'surveys']
			return
		}*/
		
		def duration = end - start
		def rate = savedSurveys*1000L*3600L/duration
		def errorSurveys = surveyCount - savedSurveys
		
		log.info "Finished in ${duration/1000} seconds"
		
		sampleSurveys.clear()
		System.gc()
		
		if (params.isAngular)
		{
			def resp = [status: status, surveyCount: savedSurveys, errorSurveys: errorSurveys, duration: duration, rate: (int)rate, test: 'postSurveys']
			render resp as JSON
		}
		else render template: 'postSurveys', model: [status: status, surveyCount: savedSurveys, errorSurveys: errorSurveys, duration: duration, rate: (int)rate, test: 'postSurveys']
	}
	
	/**
	 * Test the GET surveys endpoint by reading existing survey data (only 1 call is made)
	 * @return
	 */
	def loadTestExistingSurveys()
	{
		log.info params
		def url = getBaseUrl(params.host, params.port) + '/v1/surveys?max=50'
		log.info url
		
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
				header 'Connection', 'keep-alive'
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
			if (params.isAngular)
			{
				def resp = [status: status, test: 'surveys']
				render resp as JSON
			}
			else render template: 'error', model: [status: status, test: 'surveys']
			return
		}
		
		def json = response.json
		def totalSurveys = json.total_results
		def surveyCount = json.data.size()
		def duration = end - start
		def rate = surveyCount*1000L*3600L/duration
		
		log.info "Finished in ${duration/1000} seconds"
		
		if (params.isAngular)
		{
			def resp = [status: status, surveyCount: surveyCount, totalSurveys: totalSurveys, duration: duration, rate: (int)rate, test: 'surveys']
			render resp as JSON
		}
		else render template: 'postSurveys', model: [status: status, surveyCount: surveyCount, totalSurveys: totalSurveys, duration: duration, rate: (int)rate, test: 'surveys']
	}
	
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
}