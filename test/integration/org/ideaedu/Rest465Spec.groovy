package org.ideaedu

import grails.plugins.rest.client.RestBuilder
import grails.test.spock.IntegrationSpec
import spock.lang.Ignore

class Rest465Spec extends IntegrationSpec
{
	def restBuilder = new RestBuilder()
	
	def host = 'restprod.idea.home'
	def app = 'IDEA-CL'//'IOL3'
	def appKey = 'YB7Wngc0Msnh'//'872ttyu8d47a07c6330430lkq39500c5072bp822'
	def baseUrl = "https://${host}/IDEA-REST-SERVER"
	def reportsUrl = baseUrl + '/v1/report'
	
	def response
	def status

	@Ignore
	void "test admin report counts"()
	{
		given:
		def admin5scale = (95..104) + (120..121)
		def admin7scale = 105..119
		def reportsTested = 0
		def validReports = []
		def start = System.currentTimeMillis()
		
		and:
		def adminReportsUrl = "${reportsUrl}s?type=admin&max=20"
		
		def reportIds = restBuilder.get(adminReportsUrl) {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
						header 'Connection', 'keep-alive'
					}.json.data*.id
		
		reportIds.each
		{
			reportId ->
			def qModelUrls = admin5scale.collect {"$reportsUrl/$reportId/model/$it"} + admin7scale.collect {"$reportsUrl/$reportId/model/$it"}
			def modelUrl = "$reportsUrl/$reportId/model"
			def answered = restBuilder.get(modelUrl) {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
						header 'Connection', 'keep-alive'
					}.json.aggregate_data?.answered
			
			if (answered)
			{
				reportsTested++
				validReports << reportId
				
				qModelUrls.each
				{
					when:
					try
					{
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
					
					if (response?.json?.tally)
					{
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
						assert tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
						assert tallyCJ == dataMap[cjKey].count	// tally CJ count equals response map CJ count
						assert tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
						assert sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
						assert sumPercentages <= 100.1
					}
					
					
				}
			}
		}
		
		def end = System.currentTimeMillis()
		def time = (end - start)/1000 as int
		println "$reportsTested admin reports tested: ${validReports} in $time seconds"
	}
	
	@Ignore
	void "test chair report counts"()
	{
		given:
		def questionIds = [310, 314, 317, 319, 321] + [303, 304, 305, 306, 312, 318, 322] + [307, 308, 311, 315, 316, 322] + [305, 308, 309, 311, 313, 320, 323]
		def validReports = []
		def reportsTested = 0
		def start = System.currentTimeMillis()
		
		and:
		def chairReportsUrl = "${reportsUrl}s?type=chair&max=10"
		
		def reportIds = restBuilder.get(chairReportsUrl) {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
						header 'Connection', 'keep-alive'
					}.json.data*.id
				
		reportIds.each
		{
			reportId ->
			def qModelUrls = questionIds.collect {"$reportsUrl/$reportId/model/$it"}
			def modelUrl = "$reportsUrl/$reportId/model"
			def answered = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json.aggregate_data?.answered
		
			if (answered)
			{
				reportsTested++
				validReports << reportId
				
				qModelUrls.each
				{
					when:
					try
					{
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
					
					if (response?.json?.tally)
					{
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
						assert tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
						assert tallyCJ == dataMap[cjKey].count	// tally CJ count equals response map CJ count
						assert tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
						assert sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
						assert sumPercentages <= 100.1
					}
					
					
				}
			}
		}
		
		def end = System.currentTimeMillis()
		def time = (end - start)/1000 as int
		println "$reportsTested chair reports tested: ${validReports} in $time seconds"
	}
	
	//@Ignore
	void "test diagnostic report counts"()
	{
		given:
		def questionIds = (451..492) + (494..497)
		def validReports = []
		def reportsTested = 0
		def start = System.currentTimeMillis()
		
		and:
		def diagReportsUrl = "${reportsUrl}s?type=diagnostic&max=10"
		
		def reportIds = restBuilder.get(diagReportsUrl) {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
						header 'Connection', 'keep-alive'
					}.json.data*.id
		
		reportIds.each
		{
			reportId ->
			def qModelUrls = questionIds.collect {"$reportsUrl/$reportId/model/$it"}
			def modelUrl = "$reportsUrl/$reportId/model"
			def answered = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json.aggregate_data?.answered
		
			if (answered)
			{
				reportsTested++
				validReports << reportId
				
				qModelUrls.each
				{
					when:
					try
					{
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
					
					if (response?.json?.tally)
					{
						then:
						def tallyResponse = response.json.tally.response
						def tallyOmit = response.json.tally.omit
						def tallyCJ = response.json.tally.cannot_judge
						def tallyAll = tallyResponse + tallyOmit + tallyCJ
						def dataMap = response.json.response_option_data_map
						def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
						def sumCounts = dataMap.collect{key, value -> value.count}.sum()
						
						and:
						assert tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
						assert tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
						assert sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
						assert sumPercentages <= 100.1
					}
					
					
				}
			}
		}
		
		def end = System.currentTimeMillis()
		def time = (end - start)/1000 as int
		println "$reportsTested diagnostic reports tested: ${validReports} in $time seconds"
	}
	
	@Ignore
	void "test diagnostic 2016 report counts"()
	{
		given:
		def reportIds = [136483, 136485]
		def validReports = []
		def reportsTested = 0
		def start = System.currentTimeMillis()
		
		reportIds.each
		{
			reportId ->
			def modelUrl = "$reportsUrl/$reportId/model"
			
			def answered = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json.aggregate_data?.answered
		
			def qModelUrls = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json?.aggregate_data?.response_data_points*.question_id.collect {"$reportsUrl/$reportId/model/$it"}
		
			if (answered)
			{
				reportsTested++
				validReports << reportId
			
				qModelUrls.each
				{
					when:
					try
					{
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
					
					if (response?.json?.tally)
					{
						then:
						def tallyResponse = response.json.tally.response
						def tallyOmit = response.json.tally.omit
						def tallyCJ = response.json.tally.cannot_judge
						def tallyAll = tallyResponse + tallyOmit + tallyCJ
						def dataMap = response.json.response_option_data_map
						def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
						def sumCounts = dataMap.collect{key, value -> value.count}.sum()
						
						and:
						assert tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
						assert tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
						assert sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
						assert sumPercentages <= 100.1
					}
				}
			}
		}
		
		def end = System.currentTimeMillis()
		def time = (end - start)/1000 as int
		println "$reportsTested diagnostic 2016 reports tested: ${validReports} in $time seconds"
	}
	
	//@Ignore
	void "test short report counts"()
	{
		given:
		def validReports = []
		def reportsTested = 0
		def start = System.currentTimeMillis()
		
		and:
		def shortReportsUrl = "${reportsUrl}s?type=short&max=10"
		
		def reportIds = restBuilder.get(shortReportsUrl) {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
						header 'Connection', 'keep-alive'
					}.json.data*.id
		
		reportIds.each
		{
			reportId ->
			
			def modelUrl = "$reportsUrl/$reportId/model"
			
			def answered = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json?.aggregate_data?.answered
		
			def qModelUrls = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json?.aggregate_data?.response_data_points*.question_id.collect {"$reportsUrl/$reportId/model/$it"}
		
			if (answered)
			{
				reportsTested++
				validReports << reportId
			
				qModelUrls.each
				{
					when:
					try
					{
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
					
					if (response?.json?.tally)
					{
						then:
						def tallyResponse = response.json.tally.response
						def tallyOmit = response.json.tally.omit
						def tallyCJ = response.json.tally.cannot_judge
						def tallyAll = tallyResponse + tallyOmit + tallyCJ
						def dataMap = response.json.response_option_data_map
						def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
						def sumCounts = dataMap.collect{key, value -> value.count}.sum()
						
						and:
						assert tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
						assert tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
						assert sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
						assert sumPercentages <= 100.1
					}
				}
			}
		}
		
		def end = System.currentTimeMillis()
		def time = (end - start)/1000 as int
		println "$reportsTested short reports tested: ${validReports} in $time seconds"
	}
	
	//@Ignore
	void "test TE report counts"()
	{
		given:
		def validReports = []
		def reportsTested = 0
		def start = System.currentTimeMillis()
		
		and:
		def TEReportsUrl = "${reportsUrl}s?type=teaching essentials&max=10"
		
		def reportIds = restBuilder.get(TEReportsUrl) {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
						header 'Connection', 'keep-alive'
					}.json.data*.id
		
		reportIds.each
		{
			reportId ->
			def modelUrl = "$reportsUrl/$reportId/model"
			
			def answered = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json?.aggregate_data?.answered
		
			def qModelUrls = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json?.aggregate_data?.response_data_points*.question_id.collect {"$reportsUrl/$reportId/model/$it"}
		
			if (answered)
			{
				reportsTested++
				validReports << reportId
			
				qModelUrls.each
				{
					when:
					try
					{
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
					
					if (response?.json?.tally)
					{
						then:
						def tallyResponse = response.json.tally.response
						def tallyOmit = response.json.tally.omit
						def tallyCJ = response.json.tally.cannot_judge
						def tallyAll = tallyResponse + tallyOmit + tallyCJ
						def dataMap = response.json.response_option_data_map
						def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
						def sumCounts = dataMap.collect{key, value -> value.count}.sum()
						
						and:
						assert tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
						assert tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
						assert sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
						assert sumPercentages <= 100.1
					}
				}
			}
		}
		
		def end = System.currentTimeMillis()
		def time = (end - start)/1000 as int
		println "$reportsTested TE reports tested: ${validReports} in $time seconds"
	}
	
	//@Ignore
	void "test LE report counts"()
	{
		given:
		def validReports = []
		def reportsTested = 0
		def start = System.currentTimeMillis()
		
		and:
		def LEReportsUrl = "${reportsUrl}s?type=learning essentials&max=10"
		
		def reportIds = restBuilder.get(LEReportsUrl) {
						header 'X-IDEA-APPNAME', app
						header 'X-IDEA-KEY', appKey
						header 'Connection', 'keep-alive'
					}.json.data*.id
		
		reportIds.each
		{
			reportId ->
			def modelUrl = "$reportsUrl/$reportId/model"
			
			def answered = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json?.aggregate_data?.answered
		
			def qModelUrls = restBuilder.get(modelUrl) {
				header 'X-IDEA-APPNAME', app
				header 'X-IDEA-KEY', appKey
				header 'Connection', 'keep-alive'
			}.json?.aggregate_data?.response_data_points*.question_id.collect {"$reportsUrl/$reportId/model/$it"}
		
			if (answered)
			{
				reportsTested++
				validReports << reportId
			
				qModelUrls.each
				{
					when:
					try
					{
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
					
					if (response?.json?.tally)
					{
						then:
						def tallyResponse = response.json.tally.response
						def tallyOmit = response.json.tally.omit
						def tallyCJ = response.json.tally.cannot_judge
						def tallyAll = tallyResponse + tallyOmit + tallyCJ
						def dataMap = response.json.response_option_data_map
						def sumPercentages = dataMap.collect{key, value -> value.rate}.sum()
						def sumCounts = dataMap.collect{key, value -> value.count}.sum()
						
						and:
						assert tallyOmit == dataMap['0'].count	// tally omit count equals response map omit count
						assert tallyAll == sumCounts			// tally sum of numbers equals sum of all counts from response map
						assert sumPercentages >= 99.9			// sum of rates (percentages) is 100% ± 0.1
						assert sumPercentages <= 100.1
					}
				}
			}
		}
		
		def end = System.currentTimeMillis()
		def time = (end - start)/1000 as int
		println "$reportsTested LE reports tested: ${validReports} in $time seconds"
	}
}


/**
* Sample data:

[response_option_data_map:[3:[rate:20.0, count:2], 2:[rate:10.0, count:1], 1:[rate:20.0, count:2], 0:[rate:10.0, count:1], 6:[rate:20.0, count:2], 5:[rate:10.0, count:1], 4:[rate:10.0, count:1]], self_rating:0, 

tally:[response:7, cannot_judge:2, omit:1], results:[result:[raw:[percent_positive:28.57142857142857, mean:2.7142857142857144, percent_negative:42.857142857142854, standard_deviation:1.4960264830861913]]]]

*/