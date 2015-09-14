package org.ideaedu.loadtest

import grails.test.mixin.TestFor
import idea.data.rest.RESTSurvey
import spock.lang.Specification

@TestFor(SurveyGeneratorService)
class SurveyGeneratorServiceSpec extends Specification
{
	def setup()
	{
	}

	def cleanup()
	{
	}

	void "test buildRESTSurvey"()
	{
		expect:
		def survey = service.buildRESTSurvey(1, 9, null, true)
		//println survey
		survey.id == 0
		survey.groupId == 0
		survey.srcId.toInteger() > 0
		survey.srcGroupId.toInteger() > 0
		survey.year == 2015
		!survey.includesGapAnalysis
		survey.institutionId == 1029
		survey.institutionName == 'IDEA Center'
		survey.infoForm.respondents.size() == 1
		survey.raterForm.respondents.size() > 1
	}
	
	void "test build a large number of surveys"()
	{
		given:
		def count = 5000
		def surveys = []
		
		expect:
		for (i in 1..count)
		{
			surveys << service.buildRESTSurvey(1, 9, null, true)
		}
		
		surveys.size() == 5000
				
		cleanup: 
		surveys.clear()
		System.gc()
	}
}