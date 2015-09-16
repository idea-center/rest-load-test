package org.ideaedu.loadtest

import grails.transaction.Transactional
import idea.data.rest.RESTCourse
import idea.data.rest.RESTForm
import idea.data.rest.RESTRespondent
import idea.data.rest.RESTResponse
import idea.data.rest.RESTSurvey

@Transactional
class SurveyGeneratorService
{
	public static final int FACULTY_INFO_FORM_ID = 1
	public static final int SHORT_RATER_FORM_ID = 10
	public static final int DIAG_RATER_FORM_ID = 9

	public static final int TE_FACULTY_INFO_FORM_ID = 19
	public static final int TE_RATER_FORM_ID = 20

	public static final int ADMIN_INFO_FORM_ID = 17
	public static final int ADMIN_RATER_FORM_ID = 18

	public static final int CHAIR_INFO_FORM_ID = 13
	public static final int CHAIR_RATER_FORM_ID = 14

	private static final int MAX_ASKED = 50
	private static final int MAX_ID = 9999999

	/**
	 * Build a sample RESTSurvey that has the given information form ID and rater form ID.
	 * This will generate random data.
	 *
	 * @param infoFormID          information form ID
	 * @param raterFormID         rater form ID to use
	 * @param extraQuestionGroups List of extra question groups (RESTQuestionGroup) to be used on the rater form
	 * @param isStudentRatings    True if this is a student ratings survey and false otherwise; defaults to true.
	 * @return new RESTSurvey using a rater form with the given ID and information form with the given ID.
	 */
	def buildRESTSurvey(infoFormID, raterFormID, extraQuestionGroups, isStudentRatings=true)
	{
		def random = new Random()

		def srcId = (random.nextInt(MAX_ID) + 1).toString()
		def srcGroupId = (random.nextInt(MAX_ID) + 1).toString()
		def year = 2015
		def term = 'Spring'
		def includesGapAnalysis = false
		def startDate = new Date() - 10
		def endDate = new Date() - 1
		def creationDate = new Date() - 20
		def institutionId = 1029
		def institutionName = 'IDEA Center'
		def infoForm = buildRESTForm(infoFormID, true, null, isStudentRatings)
		def raterForm = buildRESTForm(raterFormID, false, extraQuestionGroups, isStudentRatings)

		RESTSurvey restSurvey = new RESTSurvey(
						srcId: srcId, srcGroupId: srcGroupId,
						year: year, term: term,
						includesGapAnalysis: includesGapAnalysis,
						startDate: startDate, endDate: endDate, creationDate: creationDate,
						institutionId: institutionId, institutionName: institutionName,
						infoForm: infoForm, raterForm: raterForm
						)

		if(isStudentRatings)
		{
			restSurvey.course = new RESTCourse(
							title: 'Introduction to IDEA',
							number: 'IDEA 101',
							localCode: '',
							days: 'MTWTF',
							time: '08:00'
							)
		}

		return restSurvey
	}

	/**
	 * Build a RESTForm that has the given ID and will use the flag to determine
	 * the type of the form (isInfoForm). This will include the given list of extra questions.
	 *
	 * @param formID                The ID of the form.
	 * @param isInfoForm            True if this should create an information form and false otherwise.
	 * @param extraQuestionGroups   List of extra question groups (RESTQuestionGroup) to be used on this form
	 * @param isStudentRatings      True if this is a student ratings form and false otherwise; defaults to true.
	 * @return A new RESTForm instance.
	 */
	def buildRESTForm(formID, isInfoForm, extraQuestionGroups, isStudentRatings=true)
	{
		def type
		def numAsked
		def numAnswered
		def random = new Random()

		if (isInfoForm)
		{
			type = "info"
			numAsked = 1
			numAnswered = 1
		}
		else
		{
			type = "rater"
			numAsked = random.nextInt(MAX_ASKED - 1) + 1
			numAnswered = random.nextInt(numAsked) + 1
		}

		def id = formID
		def startDate = new Date() - 5
		def endDate = new Date() - 1
		def respondents = buildRESTRespondents(numAnswered, formID, isInfoForm, isStudentRatings)

		RESTForm restForm = new RESTForm(
						id: id,
						type: type,
						startDate: startDate, endDate: endDate,
						numberAsked: numAsked,
						respondents: respondents
						)

		restForm.customQuestionGroups = extraQuestionGroups

		return restForm
	}

	/**
	 * Build the Set of RESTRespondents to use; this will create the number of respondents given (count)
	 * and use the flag to determine if they are raters or subjects (isInfoForm).
	 *
	 * @param count The number of respondents to create.
	 * @param formID The ID of the form that the respondent will respond to.
	 * @param isInfoForm True if this is generating respondents for information form and false otherwise.
	 * @param isStudentRatings True if this is a student ratings form and false otherwise; defaults to true.
	 * @return Set of RESTRespondents.
	 */
	def buildRESTRespondents(count, formID, isInfoForm, isStudentRatings=true) 
	{
		def respondents = [] as Set

		def type = "rater"
		def title
		def firstName
		def lastName

		if (isInfoForm)
		{
			type = "subject"
			firstName = "Test"
			lastName = "${type}_"

			if (isStudentRatings)
			{
				title = "Instructor"
			}
			else
			{
				title = "Dean of Test Management"
			}
		}

		if (count > 0)
		{
			for(i in 1..count)
			{
				def questions = getQuestions(formID)
				def responses = buildRESTResponses(questions)

				def respondent = new RESTRespondent(
								type: type,
								lastName: lastName ? "${lastName}${i}" : null,
								firstName: firstName,
								title: title,
								responses: responses
								)
				respondents.add(respondent)
			}
		}

		return respondents
	}

	/**
	 * Build the Set of RESTResponse instances to use.
	 */
	def buildRESTResponses(questions) 
	{
		def responses = [] as Set
		def random = new Random()

		questions?.each 
		{ 
			questionID, question ->
			
			if (questionID && question)
			{
				def answerValue
				if (question.type == 'scaled')
				{
					answerValue = question.responseOptions[random.nextInt(question.responseOptions.size())]
				}
				else
				{
					answerValue = "Some test answer"
				}

				responses.add(new RESTResponse(groupType: 'standard', questionId: questionID, answer: answerValue))
			}
		}

		return responses
	}

	/**
	 * Get the questions associated with the given form type ID.
	 *
	 * @param formID The form type ID to get the questions for.
	 * @return A map of questions (key: questionID, value: question).
	 */
	static getQuestions(formID)
	{
		def questions = FORM_QUESTIONS[formID]
		return questions
	}
	
	private static final FORM_QUESTIONS = [
		(TE_RATER_FORM_ID): [
		  726: [ type: 'scaled', responseOptions: 0..5 ],
		  727: [ type: 'scaled', responseOptions: 0..5 ],
		  728: [ type: 'scaled', responseOptions: 0..5 ],
		  729: [ type: 'scaled', responseOptions: 0..5 ],
		  730: [ type: 'scaled', responseOptions: 0..5 ],
		  731: [ type: 'scaled', responseOptions: 0..5 ],
		  732: [ type: 'scaled', responseOptions: 0..5 ],
		  733: [ type: 'scaled', responseOptions: 0..5 ],
		  734: [ type: 'scaled', responseOptions: 0..5 ],
		  735: [ type: 'scaled', responseOptions: 0..5 ],
		  736: [ type: 'scaled', responseOptions: 0..5 ],
		  737: [ type: 'scaled', responseOptions: 0..5 ]
		],
		(TE_FACULTY_INFO_FORM_ID): [/* There are no questions in the Teaching Essentials information form. */],
  
		(SHORT_RATER_FORM_ID): [
		  669: [ type: 'scaled', responseOptions: 0..5 ],
		  670: [ type: 'scaled', responseOptions: 0..5 ],
		  671: [ type: 'scaled', responseOptions: 0..5 ],
		  672: [ type: 'scaled', responseOptions: 0..5 ],
		  673: [ type: 'scaled', responseOptions: 0..5 ],
		  674: [ type: 'scaled', responseOptions: 0..5 ],
		  675: [ type: 'scaled', responseOptions: 0..5 ],
		  676: [ type: 'scaled', responseOptions: 0..5 ],
		  677: [ type: 'scaled', responseOptions: 0..5 ],
		  678: [ type: 'scaled', responseOptions: 0..5 ],
		  679: [ type: 'scaled', responseOptions: 0..5 ],
		  680: [ type: 'scaled', responseOptions: 0..5 ],
		  681: [ type: 'scaled', responseOptions: 0..5 ],
		  682: [ type: 'scaled', responseOptions: 0..5 ],
		  683: [ type: 'scaled', responseOptions: 0..5 ],
		  684: [ type: 'scaled', responseOptions: 0..5 ],
		  685: [ type: 'scaled', responseOptions: 0..5 ],
		  686: [ type: 'scaled', responseOptions: 0..5 ],
		  687: [ type: 'open' ]
		],
		1: [ // SHORT_FORM_INFO_FORM_ID and DIAG_FORM_INFO_FORM_ID
		  566: [ type: 'scaled', responseOptions: 0..3 ],
		  567: [ type: 'scaled', responseOptions: 0..3 ],
		  568: [ type: 'scaled', responseOptions: 0..3 ],
		  569: [ type: 'scaled', responseOptions: 0..3 ],
		  570: [ type: 'scaled', responseOptions: 0..3 ],
		  571: [ type: 'scaled', responseOptions: 0..3 ],
		  572: [ type: 'scaled', responseOptions: 0..3 ],
		  573: [ type: 'scaled', responseOptions: 0..3 ],
		  574: [ type: 'scaled', responseOptions: 0..3 ],
		  575: [ type: 'scaled', responseOptions: 0..3 ],
		  576: [ type: 'scaled', responseOptions: 0..3 ],
		  577: [ type: 'scaled', responseOptions: 0..3 ],
		  578: [ type: 'scaled', responseOptions: 0..10 ],
		  579: [ type: 'scaled', responseOptions: 0..10 ],
		  580: [ type: 'scaled', responseOptions: 0..3 ],
		  581: [ type: 'scaled', responseOptions: 0..3 ],
		  582: [ type: 'scaled', responseOptions: 0..3 ],
		  583: [ type: 'scaled', responseOptions: 0..3 ],
		  584: [ type: 'scaled', responseOptions: 0..3 ],
		  585: [ type: 'scaled', responseOptions: 0..3 ],
		  586: [ type: 'scaled', responseOptions: 0..3 ],
		  587: [ type: 'scaled', responseOptions: 0..3 ],
		  588: [ type: 'scaled', responseOptions: 0..3 ],
		  589: [ type: 'scaled', responseOptions: 0..4 ],
		  590: [ type: 'scaled', responseOptions: 0..4 ],
		  591: [ type: 'scaled', responseOptions: 0..4 ],
		  592: [ type: 'scaled', responseOptions: 0..4 ],
		  593: [ type: 'scaled', responseOptions: 0..4 ],
		  594: [ type: 'scaled', responseOptions: 0..4 ],
		  595: [ type: 'scaled', responseOptions: 0..4 ],
		  596: [ type: 'scaled', responseOptions: 0..4 ],
		  597: [ type: 'scaled', responseOptions: 0..4 ],
		  598: [ type: 'scaled', responseOptions: 0..6 ],
		  599: [ type: 'scaled', responseOptions: 0..2 ],
		  600: [ type: 'scaled', responseOptions: 0..2 ],
		],
  
		(DIAG_RATER_FORM_ID): [
		  451: [ type: 'scaled', responseOptions: 0..5 ],
		  452: [ type: 'scaled', responseOptions: 0..5 ],
		  453: [ type: 'scaled', responseOptions: 0..5 ],
		  454: [ type: 'scaled', responseOptions: 0..5 ],
		  455: [ type: 'scaled', responseOptions: 0..5 ],
		  456: [ type: 'scaled', responseOptions: 0..5 ],
		  457: [ type: 'scaled', responseOptions: 0..5 ],
		  458: [ type: 'scaled', responseOptions: 0..5 ],
		  459: [ type: 'scaled', responseOptions: 0..5 ],
		  460: [ type: 'scaled', responseOptions: 0..5 ],
		  461: [ type: 'scaled', responseOptions: 0..5 ],
		  462: [ type: 'scaled', responseOptions: 0..5 ],
		  463: [ type: 'scaled', responseOptions: 0..5 ],
		  464: [ type: 'scaled', responseOptions: 0..5 ],
		  465: [ type: 'scaled', responseOptions: 0..5 ],
		  466: [ type: 'scaled', responseOptions: 0..5 ],
		  467: [ type: 'scaled', responseOptions: 0..5 ],
		  468: [ type: 'scaled', responseOptions: 0..5 ],
		  469: [ type: 'scaled', responseOptions: 0..5 ],
		  470: [ type: 'scaled', responseOptions: 0..5 ],
		  471: [ type: 'scaled', responseOptions: 0..5 ],
		  472: [ type: 'scaled', responseOptions: 0..5 ],
		  473: [ type: 'scaled', responseOptions: 0..5 ],
		  474: [ type: 'scaled', responseOptions: 0..5 ],
		  475: [ type: 'scaled', responseOptions: 0..5 ],
		  476: [ type: 'scaled', responseOptions: 0..5 ],
		  477: [ type: 'scaled', responseOptions: 0..5 ],
		  478: [ type: 'scaled', responseOptions: 0..5 ],
		  479: [ type: 'scaled', responseOptions: 0..5 ],
		  480: [ type: 'scaled', responseOptions: 0..5 ],
		  481: [ type: 'scaled', responseOptions: 0..5 ],
		  482: [ type: 'scaled', responseOptions: 0..5 ],
		  483: [ type: 'scaled', responseOptions: 0..5 ],
		  484: [ type: 'scaled', responseOptions: 0..5 ],
		  485: [ type: 'scaled', responseOptions: 0..5 ],
		  486: [ type: 'scaled', responseOptions: 0..5 ],
		  487: [ type: 'scaled', responseOptions: 0..5 ],
		  488: [ type: 'scaled', responseOptions: 0..5 ],
		  489: [ type: 'scaled', responseOptions: 0..5 ],
		  490: [ type: 'scaled', responseOptions: 0..5 ],
		  491: [ type: 'scaled', responseOptions: 0..5 ],
		  492: [ type: 'scaled', responseOptions: 0..5 ],
		  493: [ type: 'scaled', responseOptions: 0..5 ],
		  494: [ type: 'scaled', responseOptions: 0..5 ],
		  495: [ type: 'scaled', responseOptions: 0..5 ],
		  496: [ type: 'scaled', responseOptions: 0..5 ],
		  497: [ type: 'scaled', responseOptions: 0..5 ],
		  498: [ type: 'open' ]
		],
  
		(ADMIN_RATER_FORM_ID): [
		  95: [ type: 'scaled', responseOptions: 0..5 ],
		  96: [ type: 'scaled', responseOptions: 0..5 ],
		  97: [ type: 'scaled', responseOptions: 0..5 ],
		  98: [ type: 'scaled', responseOptions: 0..5 ],
		  99: [ type: 'scaled', responseOptions: 0..5 ],
		  100: [ type: 'scaled', responseOptions: 0..5 ],
		  101: [ type: 'scaled', responseOptions: 0..5 ],
		  102: [ type: 'scaled', responseOptions: 0..5 ],
		  103: [ type: 'scaled', responseOptions: 0..5 ],
		  104: [ type: 'scaled', responseOptions: 0..5 ],
		  105: [ type: 'scaled', responseOptions: 0..8 ],
		  106: [ type: 'scaled', responseOptions: 0..8 ],
		  107: [ type: 'scaled', responseOptions: 0..8 ],
		  108: [ type: 'scaled', responseOptions: 0..8 ],
		  109: [ type: 'scaled', responseOptions: 0..8 ],
		  110: [ type: 'scaled', responseOptions: 0..8 ],
		  111: [ type: 'scaled', responseOptions: 0..8 ],
		  112: [ type: 'scaled', responseOptions: 0..8 ],
		  113: [ type: 'scaled', responseOptions: 0..8 ],
		  114: [ type: 'scaled', responseOptions: 0..8 ],
		  115: [ type: 'scaled', responseOptions: 0..8 ],
		  116: [ type: 'scaled', responseOptions: 0..8 ],
		  117: [ type: 'scaled', responseOptions: 0..8 ],
		  118: [ type: 'scaled', responseOptions: 0..8 ],
		  119: [ type: 'scaled', responseOptions: 0..8 ],
		  120: [ type: 'scaled', responseOptions: 0..6 ],
		  121: [ type: 'scaled', responseOptions: 0..6 ],
		  122: [ type: 'open' ],
		  123: [ type: 'open' ],
		  124: [ type: 'open' ]
		],
		(ADMIN_INFO_FORM_ID): [
		  24: [ type: 'scaled', responseOptions: 0..5 ],
		  25: [ type: 'scaled', responseOptions: 0..5 ],
		  26: [ type: 'scaled', responseOptions: 0..5 ],
		  27: [ type: 'scaled', responseOptions: 0..5 ],
		  28: [ type: 'scaled', responseOptions: 0..5 ],
		  29: [ type: 'scaled', responseOptions: 0..5 ],
		  30: [ type: 'scaled', responseOptions: 0..5 ],
		  31: [ type: 'scaled', responseOptions: 0..5 ],
		  32: [ type: 'scaled', responseOptions: 0..5 ],
		  33: [ type: 'scaled', responseOptions: 0..5 ],
		  34: [ type: 'scaled', responseOptions: 0..7 ],
		  35: [ type: 'scaled', responseOptions: 0..7 ],
		  36: [ type: 'scaled', responseOptions: 0..7 ],
		  37: [ type: 'scaled', responseOptions: 0..7 ],
		  38: [ type: 'scaled', responseOptions: 0..7 ],
		  39: [ type: 'scaled', responseOptions: 0..7 ],
		  40: [ type: 'scaled', responseOptions: 0..7 ],
		  41: [ type: 'scaled', responseOptions: 0..7 ],
		  42: [ type: 'scaled', responseOptions: 0..7 ],
		  43: [ type: 'scaled', responseOptions: 0..7 ],
		  44: [ type: 'scaled', responseOptions: 0..7 ],
		  45: [ type: 'scaled', responseOptions: 0..7 ],
		  46: [ type: 'scaled', responseOptions: 0..7 ],
		  47: [ type: 'scaled', responseOptions: 0..7 ],
		  48: [ type: 'scaled', responseOptions: 0..7 ],
		  49: [ type: 'scaled', responseOptions: 0..5 ],
		  50: [ type: 'scaled', responseOptions: 0..5 ],
		  51: [ type: 'scaled', responseOptions: 0..3 ],
		  52: [ type: 'open' ],
		  53: [ type: 'open' ],
		  54: [ type: 'open' ],
		  55: [ type: 'open' ],
		  56: [ type: 'open' ],
		  57: [ type: 'open' ]
		],
  
		(CHAIR_RATER_FORM_ID): [
		  303: [ type: 'scaled', responseOptions: 0..6 ],
		  304: [ type: 'scaled', responseOptions: 0..6 ],
		  305: [ type: 'scaled', responseOptions: 0..6 ],
		  306: [ type: 'scaled', responseOptions: 0..6 ],
		  307: [ type: 'scaled', responseOptions: 0..6 ],
		  308: [ type: 'scaled', responseOptions: 0..6 ],
		  309: [ type: 'scaled', responseOptions: 0..6 ],
		  310: [ type: 'scaled', responseOptions: 0..6 ],
		  311: [ type: 'scaled', responseOptions: 0..6 ],
		  312: [ type: 'scaled', responseOptions: 0..6 ],
		  313: [ type: 'scaled', responseOptions: 0..6 ],
		  314: [ type: 'scaled', responseOptions: 0..6 ],
		  315: [ type: 'scaled', responseOptions: 0..6 ],
		  316: [ type: 'scaled', responseOptions: 0..6 ],
		  317: [ type: 'scaled', responseOptions: 0..6 ],
		  318: [ type: 'scaled', responseOptions: 0..6 ],
		  319: [ type: 'scaled', responseOptions: 0..6 ],
		  320: [ type: 'scaled', responseOptions: 0..6 ],
		  321: [ type: 'scaled', responseOptions: 0..6 ],
		  322: [ type: 'scaled', responseOptions: 0..6 ],
		  323: [ type: 'scaled', responseOptions: 0..6 ],
		  324: [ type: 'scaled', responseOptions: 0..6 ],
		  325: [ type: 'scaled', responseOptions: 0..6 ],
		  326: [ type: 'scaled', responseOptions: 0..6 ],
		  327: [ type: 'scaled', responseOptions: 0..6 ],
		  328: [ type: 'scaled', responseOptions: 0..6 ],
		  329: [ type: 'scaled', responseOptions: 0..6 ],
		  330: [ type: 'scaled', responseOptions: 0..6 ],
		  331: [ type: 'scaled', responseOptions: 0..6 ],
		  332: [ type: 'scaled', responseOptions: 0..6 ],
		  333: [ type: 'scaled', responseOptions: 0..6 ],
		  334: [ type: 'scaled', responseOptions: 0..6 ],
		  335: [ type: 'scaled', responseOptions: 0..6 ],
		  336: [ type: 'scaled', responseOptions: 0..6 ],
		  337: [ type: 'scaled', responseOptions: 0..6 ],
		  338: [ type: 'scaled', responseOptions: 0..6 ],
		  339: [ type: 'scaled', responseOptions: 0..6 ],
		  340: [ type: 'scaled', responseOptions: 0..6 ],
		  341: [ type: 'scaled', responseOptions: 0..6 ],
		  342: [ type: 'scaled', responseOptions: 0..6 ],
		  343: [ type: 'scaled', responseOptions: 0..6 ],
		  344: [ type: 'scaled', responseOptions: 0..6 ],
		  345: [ type: 'scaled', responseOptions: 0..6 ],
		  346: [ type: 'scaled', responseOptions: 0..6 ],
		  347: [ type: 'scaled', responseOptions: 0..6 ],
		  348: [ type: 'scaled', responseOptions: 0..6 ],
		  349: [ type: 'scaled', responseOptions: 0..6 ],
		  350: [ type: 'scaled', responseOptions: 0..6 ],
		  351: [ type: 'scaled', responseOptions: 0..6 ],
		  352: [ type: 'scaled', responseOptions: 0..6 ],
		  353: [ type: 'scaled', responseOptions: 0..6 ],
		  354: [ type: 'scaled', responseOptions: 0..6 ],
		  355: [ type: 'scaled', responseOptions: 0..6 ],
		  356: [ type: 'scaled', responseOptions: 0..6 ],
		  357: [ type: 'scaled', responseOptions: 0..6 ],
		  358: [ type: 'open' ],
		  359: [ type: 'open' ],
		  360: [ type: 'open' ],
		  361: [ type: 'open' ]
		],
		(CHAIR_INFO_FORM_ID): [
		  155: [ type: 'scaled', responseOptions: 0..3 ],
		  156: [ type: 'scaled', responseOptions: 0..3 ],
		  157: [ type: 'scaled', responseOptions: 0..3 ],
		  158: [ type: 'scaled', responseOptions: 0..3 ],
		  159: [ type: 'scaled', responseOptions: 0..3 ],
		  160: [ type: 'scaled', responseOptions: 0..3 ],
		  161: [ type: 'scaled', responseOptions: 0..3 ],
		  162: [ type: 'scaled', responseOptions: 0..3 ],
		  163: [ type: 'scaled', responseOptions: 0..3 ],
		  164: [ type: 'scaled', responseOptions: 0..3 ],
		  165: [ type: 'scaled', responseOptions: 0..3 ],
		  166: [ type: 'scaled', responseOptions: 0..3 ],
		  167: [ type: 'scaled', responseOptions: 0..3 ],
		  168: [ type: 'scaled', responseOptions: 0..3 ],
		  169: [ type: 'scaled', responseOptions: 0..3 ],
		  170: [ type: 'scaled', responseOptions: 0..3 ],
		  171: [ type: 'scaled', responseOptions: 0..3 ],
		  172: [ type: 'scaled', responseOptions: 0..3 ],
		  173: [ type: 'scaled', responseOptions: 0..3 ],
		  174: [ type: 'scaled', responseOptions: 0..3 ],
		  175: [ type: 'scaled', responseOptions: 0..3 ],
		  176: [ type: 'scaled', responseOptions: 0..5 ],
		  177: [ type: 'scaled', responseOptions: 0..5 ],
		  178: [ type: 'scaled', responseOptions: 0..5 ],
		  179: [ type: 'scaled', responseOptions: 0..5 ],
		  180: [ type: 'scaled', responseOptions: 0..5 ],
		  181: [ type: 'scaled', responseOptions: 0..5 ],
		  182: [ type: 'scaled', responseOptions: 0..5 ],
		  183: [ type: 'scaled', responseOptions: 0..5 ],
		  184: [ type: 'scaled', responseOptions: 0..5 ],
		  185: [ type: 'scaled', responseOptions: 0..5 ],
		  186: [ type: 'scaled', responseOptions: 0..5 ],
		  187: [ type: 'scaled', responseOptions: 0..5 ],
		  188: [ type: 'scaled', responseOptions: 0..5 ],
		  189: [ type: 'scaled', responseOptions: 0..5 ],
		  190: [ type: 'scaled', responseOptions: 0..5 ],
		  191: [ type: 'scaled', responseOptions: 0..5 ],
		  192: [ type: 'scaled', responseOptions: 0..5 ],
		  193: [ type: 'scaled', responseOptions: 0..5 ],
		  194: [ type: 'scaled', responseOptions: 0..5 ],
		  195: [ type: 'scaled', responseOptions: 0..5 ],
		  196: [ type: 'scaled', responseOptions: 0..5 ],
		  197: [ type: 'scaled', responseOptions: 0..5 ],
		  198: [ type: 'scaled', responseOptions: 0..5 ],
		  199: [ type: 'scaled', responseOptions: 0..5 ],
		  200: [ type: 'scaled', responseOptions: 0..5 ],
		  201: [ type: 'scaled', responseOptions: 0..5 ],
		  202: [ type: 'scaled', responseOptions: 0..5 ],
		  203: [ type: 'scaled', responseOptions: 0..5 ],
		  204: [ type: 'scaled', responseOptions: 0..5 ],
		  205: [ type: 'scaled', responseOptions: 0..5 ],
		  206: [ type: 'scaled', responseOptions: 0..5 ],
		  207: [ type: 'scaled', responseOptions: 0..5 ],
		  208: [ type: 'scaled', responseOptions: 0..5 ],
		  209: [ type: 'scaled', responseOptions: 0..5 ],
		  210: [ type: 'scaled', responseOptions: 0..5 ],
		  211: [ type: 'scaled', responseOptions: 0..5 ],
		  212: [ type: 'scaled', responseOptions: 0..5 ],
		  213: [ type: 'scaled', responseOptions: 0..5 ],
		  214: [ type: 'scaled', responseOptions: 0..5 ],
		  215: [ type: 'scaled', responseOptions: 0..5 ],
		  216: [ type: 'scaled', responseOptions: 0..5 ],
		  217: [ type: 'scaled', responseOptions: 0..5 ],
		  218: [ type: 'scaled', responseOptions: 0..5 ],
		  219: [ type: 'scaled', responseOptions: 0..5 ],
		  220: [ type: 'scaled', responseOptions: 0..5 ],
		  221: [ type: 'scaled', responseOptions: 0..5 ],
		  222: [ type: 'scaled', responseOptions: 0..5 ],
		  223: [ type: 'scaled', responseOptions: 0..5 ],
		  224: [ type: 'scaled', responseOptions: 0..5 ],
		  225: [ type: 'scaled', responseOptions: 0..5 ],
		  226: [ type: 'scaled', responseOptions: 0..5 ],
		  227: [ type: 'scaled', responseOptions: 0..5 ],
		  228: [ type: 'scaled', responseOptions: 0..5 ],
		  229: [ type: 'scaled', responseOptions: 0..4 ],
		  230: [ type: 'open' ],
		  231: [ type: 'open' ],
		  232: [ type: 'scaled', responseOptions: 0..5 ]
		]
	  ]
}
