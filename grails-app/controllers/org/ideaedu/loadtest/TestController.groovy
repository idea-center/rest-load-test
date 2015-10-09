package org.ideaedu.loadtest

import static grails.async.Promises.*

class TestController
{
	def test()
	{
		render view: 'test'
	}
	
	def testAsync()
	{
		/*task
		{
			Thread.sleep(2000)
			render template: 'results', model: [result: 'Done']
		}*/
		
		def ctx = startAsync()
		ctx.start
		{
			println 'startint work'
			Thread.sleep(2000)
			println 'Finished work'
			render template: 'results', model: [result: 'Done']
			ctx.dispatch()
		}
	}
	
	def testAsyncAngular()
	{
		def ctx = startAsync()
		ctx.start
		{
			println 'startint work'
			Thread.sleep(2000)
			println 'Finished work'
			render 'Done'
			ctx.dispatch()
		}
	}
}
