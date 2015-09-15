package org.ideaedu.loadtest

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import groovyx.gpars.GParsPool

class PostSurveysController
{
	def restBuilder = new RestBuilder()
	def app = 'IOL3'
	def appKey = '872ttyu8d47a07c6330430lkq39500c5072bp822'
	def jsonContent = 'application/json;charset=utf-8'
	
	def surveyGeneratorService
	
	def static SURVEY_REPORTS_THREADS = 1
	def static SURVEY_COUNT = 1

	def loadTestPostSurveys()
	{
		println params
		def url = getBaseUrl(params.host, params.port) + '/v1/services/survey'
		println url
		
		def surveyCount = params.surveyCount ?: SURVEY_COUNT
		surveyCount = surveyCount as int
		if (surveyCount < 1) surveyCount = SURVEY_COUNT
		
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
		def savedSurveys = 0
		def errorSurveys = 0
		
		println "Using $sthreads thread(s) to post surveys"
		
		println "Starting timer..."
		def start = System.currentTimeMillis()
		
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
		
		println sampleSurveys.size()
		render template: 'postSurveys', model: [status: status, surveyCount: savedSurveys, errorSurveys: errorSurveys, duration: duration, rate: (int)rate, test: 'postSurveys']
	}
	
	def loadTestExistingSurveys()
	{
		println params
		def url = getBaseUrl(params.host, params.port) + '/v1/surveys'
		println url
		
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
		def surveyCount = json.total_results
		def duration = end - start
		def rate = surveyCount*1000L*3600L/duration
		
		//println json
		
		render template: 'postSurveys', model: [status: status, surveyCount: surveyCount, duration: duration, rate: (int)rate, test: 'surveys']
	}
	
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
}