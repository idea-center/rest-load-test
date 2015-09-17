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
	
	// using more than 500 generated surveys is likely to cause out of memory errors
	def static MAX_SURVEYS_TO_POST = 5000

	/**
	 * Tests the POST surveys endpoint by sending generated json
	 * @return
	 */
	def loadTestPostSurveys()
	{
		println params
		def url = getBaseUrl(params.host, params.port) + '/v1/services/survey'
		println url
		
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
			sampleSurveys << surveyGeneratorService.buildRESTSurvey(1, 9, null, true)
		}
		
		def response
		def status
		
		// counts surveys successfully saved
		def savedSurveys = 0
		
		// counts surveys that could not be saved on the REST server
		def errorSurveys = 0
		
		println "Using $sthreads thread(s) to post ${surveyCount} surveys"
		
		println "Starting timer..."
		def start = System.currentTimeMillis()
		
		// POST survey data in parallel by calling the endpoint via a thread pool
		// TODO: determine if synchronization is necessary here
		GParsPool.withPool sthreads,
		{
			sampleSurveys.eachParallel
			{ 
				surveyData ->

				try
				{
					response = restBuilder.post(url)
					{
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
						header 'Content-Type', jsonContent
						json 	surveyData.toJSON()
					}

					status = response.status

					if (status == 200) savedSurveys++
					else errorSurveys++
				}
				catch(e)
				{
					status = 'Connection timed out'
				}
			}
		}

		def end = System.currentTimeMillis()
		println "Ending timer"
		
		def duration = end - start
		def rate = savedSurveys*1000L*3600L/duration
		
		sampleSurveys.clear()
		System.gc()
		
		render template: 'postSurveys', model: [status: status, surveyCount: savedSurveys, errorSurveys: errorSurveys, duration: duration, rate: (int)rate, test: 'postSurveys']
	}
	
	/**
	 * Test the GET surveys endpoint by reading existing survey data (only 1 call is made)
	 * @return
	 */
	def loadTestExistingSurveys()
	{
		println params
		def url = getBaseUrl(params.host, params.port) + '/v1/surveys?max=50'
		println url
		
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
			render template: 'error', model: [status: status, test: 'surveys']
			return
		}
		
		def json = response.json
		def totalSurveys = json.total_results
		def surveyCount = json.data.size()
		def duration = end - start
		def rate = surveyCount*1000L*3600L/duration
		
		render template: 'postSurveys', model: [status: status, surveyCount: surveyCount, totalSurveys: totalSurveys, duration: duration, rate: (int)rate, test: 'surveys']
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