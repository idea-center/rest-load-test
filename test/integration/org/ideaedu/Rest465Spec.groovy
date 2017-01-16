package org.ideaedu

import grails.plugins.rest.client.RestBuilder
import grails.test.spock.IntegrationSpec

class Rest465Spec extends IntegrationSpec
{
	def restBuilder = new RestBuilder()
	
	def host = 'reststage.ideasystem.org'
	def port = 80
	def app = 'IOL3'
	def appKey = '872ttyu8d47a07c6330430lkq39500c5072bp822'
	
	def baseUrl = "http://${host}:${port}/IDEA-REST-SERVER"
	
	def reportsUrl = baseUrl + '/v1/report'
	
	def response
	def status

	void "test admin report counts"()
	{
		given:
		def surveyIds = [7, 9, 11]
		def admin5scale = (95..104) + (120..121)
		def admin7scale = 105..119
		
		surveyIds.each
		{
			surveyId ->
			def qModelUrls = admin5scale.collect {"$reportsUrl/$surveyId/model/$it"} + admin7scale.collect {"$reportsUrl/$surveyId/model/$it"}
			
			qModelUrls.each
			{
				when:
				try
				{
					println it
					response = restBuilder.get(it) {
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
				
				then:
				def tallyResponse = response.json.tally.response
				def tallyOmit = response.json.tally.omit
				def tallyCJ = response.json.tally.cannot_judge
				def tallyAll = tallyResponse + tallyOmit + tallyCJ
				def dataMap = response.json.response_option_data_map
				def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
				def sumCounts = dataMap.collect{key, value -> value.count}.sum()
				
				def scale = dataMap.keySet().size()
				def cjKey = scale == 7 ? '6' : '8'
				
				and:
				tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
				tallyCJ == dataMap[cjKey].count	// tally CJ count equals response map CJ count
				tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
				sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
				sumPercentages <= 100.1
			}
		}
	}
	
	void "test chair report counts"()
	{
		given:
		def surveyIds = [13]
		def questionIds = [310, 314, 317, 319, 321] + [303, 304, 305, 306, 312, 318, 322] + [307, 308, 311, 315, 316, 322] + [305, 308, 309, 311, 313, 320, 323]
		
		surveyIds.each
		{
			surveyId ->
			def qModelUrls = questionIds.collect {"$reportsUrl/$surveyId/model/$it"}
			
			qModelUrls.each
			{
				when:
				try
				{
					println it
					response = restBuilder.get(it) {
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
				
				then:
				def tallyResponse = response.json.tally.response
				def tallyOmit = response.json.tally.omit
				def tallyCJ = response.json.tally.cannot_judge
				def tallyAll = tallyResponse + tallyOmit + tallyCJ
				def dataMap = response.json.response_option_data_map
				def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
				def sumCounts = dataMap.collect{key, value -> value.count}.sum()
				def cjKey = '6'
				
				and:
				tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
				tallyCJ == dataMap[cjKey].count	// tally CJ count equals response map CJ count
				tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
				sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
				sumPercentages <= 100.1
			}
		}
	}
	
	void "test diagnostic 2.1 report counts"()
	{
		given:
		def surveyIds = [15]
		def questionIds = (451..492) + (494..497)
		
		surveyIds.each
		{
			surveyId ->
			def qModelUrls = questionIds.collect {"$reportsUrl/$surveyId/model/$it"}
			
			qModelUrls.each
			{
				when:
				try
				{
					println it
					response = restBuilder.get(it) {
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
				
				then:
				def tallyResponse = response.json.tally.response
				def tallyOmit = response.json.tally.omit
				def tallyCJ = response.json.tally.cannot_judge
				def tallyAll = tallyResponse + tallyOmit + tallyCJ
				def dataMap = response.json.response_option_data_map
				def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
				def sumCounts = dataMap.collect{key, value -> value.count}.sum()
				
				and:
				tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
				tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
				sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
				sumPercentages <= 100.1
			}
		}
	}
	
	void "test diagnostic 2016 report counts"()
	{
		given:
		def surveyIds = [17]
		def questionIds = (499..538)
		
		surveyIds.each
		{
			surveyId ->
			def qModelUrls = questionIds.collect {"$reportsUrl/$surveyId/model/$it"}
			
			qModelUrls.each
			{
				when:
				try
				{
					println it
					response = restBuilder.get(it) {
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
				
				then:
				def tallyResponse = response.json.tally.response
				def tallyOmit = response.json.tally.omit
				def tallyCJ = response.json.tally.cannot_judge
				def tallyAll = tallyResponse + tallyOmit + tallyCJ
				def dataMap = response.json.response_option_data_map
				def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
				def sumCounts = dataMap.collect{key, value -> value.count}.sum()
				
				and:
				tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
				tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
				sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
				sumPercentages <= 100.1
			}
		}
	}
	
	void "test short report counts"()
	{
		given:
		def surveyIds = [19]
		def questionIds = (742..759)
		
		surveyIds.each
		{
			surveyId ->
			def qModelUrls = questionIds.collect {"$reportsUrl/$surveyId/model/$it"}
			
			qModelUrls.each
			{
				when:
				try
				{
					println it
					response = restBuilder.get(it) {
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
				
				then:
				def tallyResponse = response.json.tally.response
				def tallyOmit = response.json.tally.omit
				def tallyCJ = response.json.tally.cannot_judge
				def tallyAll = tallyResponse + tallyOmit + tallyCJ
				def dataMap = response.json.response_option_data_map
				def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
				def sumCounts = dataMap.collect{key, value -> value.count}.sum()
				
				and:
				tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
				tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
				sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
				sumPercentages <= 100.1
			}
		}
	}
	
	void "test TE report counts"()
	{
		given:
		def surveyIds = [21]
		def questionIds = (799..810)
		
		surveyIds.each
		{
			surveyId ->
			def qModelUrls = questionIds.collect {"$reportsUrl/$surveyId/model/$it"}
			
			qModelUrls.each
			{
				when:
				try
				{
					println it
					response = restBuilder.get(it) {
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
				
				then:
				def tallyResponse = response.json.tally.response
				def tallyOmit = response.json.tally.omit
				def tallyCJ = response.json.tally.cannot_judge
				def tallyAll = tallyResponse + tallyOmit + tallyCJ
				def dataMap = response.json.response_option_data_map
				def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
				def sumCounts = dataMap.collect{key, value -> value.count}.sum()
				
				and:
				tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
				tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
				sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
				sumPercentages <= 100.1
			}
		}
	}
	
	void "test LE report counts"()
	{
		given:
		def surveyIds = [22]
		def questionIds = (723..740)
		
		surveyIds.each
		{
			surveyId ->
			def qModelUrls = questionIds.collect {"$reportsUrl/$surveyId/model/$it"}
			
			qModelUrls.each
			{
				when:
				try
				{
					println it
					response = restBuilder.get(it) {
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
				
				then:
				def tallyResponse = response.json.tally.response
				def tallyOmit = response.json.tally.omit
				def tallyCJ = response.json.tally.cannot_judge
				def tallyAll = tallyResponse + tallyOmit + tallyCJ
				def dataMap = response.json.response_option_data_map
				def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
				def sumCounts = dataMap.collect{key, value -> value.count}.sum()
				
				and:
				tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
				tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
				sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
				sumPercentages <= 100.1
			}
		}
	}
}


/**
* Sample data:

[response_option_data_map:[3:[rate:20.0, count:2], 2:[rate:10.0, count:1], 1:[rate:20.0, count:2], 0:[rate:10.0, count:1], 6:[rate:20.0, count:2], 5:[rate:10.0, count:1], 4:[rate:10.0, count:1]], self_rating:0, 

tally:[response:7, cannot_judge:2, omit:1], results:[result:[raw:[percent_positive:28.57142857142857, mean:2.7142857142857144, percent_negative:42.857142857142854, standard_deviation:1.4960264830861913]]]]

*/