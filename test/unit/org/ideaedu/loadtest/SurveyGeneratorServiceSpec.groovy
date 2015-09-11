package org.ideaedu.loadtest

import grails.test.mixin.TestFor
import idea.data.rest.RESTSurvey
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
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
		println survey
		
		/*when:
		def propsMap = survey.properties
		propsMap.remove('metaClass')
		propsMap.remove('class')
		propsMap.remove('gson')
		
		def clone = new RESTSurvey(propsMap)
		
		then:
		println clone*/
	}
}