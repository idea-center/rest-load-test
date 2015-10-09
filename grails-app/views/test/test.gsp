<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="main" />
<title>IDEA REST SERVER Load Test Application</title>
<g:javascript>
	var getReportsUrl = "${createLink(controller: 'getReports', action: 'loadTestExistingReportsAngular')}";
	var getReportModelsUrl = "${createLink(controller: 'getReports', action: 'loadTestExistingReportModelsAngular')}";
	var getReportQuestionModelsUrl = "${createLink(controller: 'getReports', action: 'loadTestExistingReportModelsAndQuestionsAngular')}";
	var getSurveysUrl = "${createLink(controller: 'postSurveys', action: 'loadTestExistingSurveys')}";
	var postSurveysUrl = "${createLink(controller: 'postSurveys', action: 'loadTestPostSurveys')}";
</g:javascript>
</head>

<body>
	<ng-form class="form form-horizontal" ng-controller="GetReportsController">
		<div class="row" style="margin-top: 0;">
			<div class="col-md-3">
				<label for="hostName" class="control-label">Host:</label> <input type="text" id="hostName" name="host" class="form-control" placeholder="default: localhost" ng-model="params.host">
			</div>
			<div class="col-md-2">
				<label for="portNumber" class="control-label">Port:</label> <input type="text" id="portNumber" name="port" class="form-control" placeholder="default: 8091" ng-model="params.port">
			</div>
			<div class="col-md-2">
				<label for="appName" class="control-label">App name <small>(required)</small>:
				</label> <input type="text" id="appName" name="appName" class="form-control" placeholder="enter app name" ng-model="params.appName">
			</div>
			<div class="col-md-5">
				<label for="appKey" class="control-label">App key <small>(required)</small>:
				</label> <input type="text" id="appKey" name="appKey" class="form-control" placeholder="enter app key" ng-model="params.appKey">
			</div>
		</div>

		<div class="col-lg-6" id="reportsTesting">
			<div class="alert alert-success">
				<b>TEST Reports</b> (GET report metadata, report models)
			</div>

			<div class="row form-group ">
				<div class="form-inline">
					<label for="reportThreads" class="col-md-6">Report model threads:</label> <input type="number" min="1" max="20" value="1" id="reportThreads" name="reportThreads" class="form-control col-md-2" ng-model="params.reportThreads">
				</div>
				<div class="form-inline">
					<label for="questionThreads" class="col-md-6">Question model threads:</label> <input type="number" min="1" max="20" value="1" id="questionThreads" name="questionThreads" ng-model="params.questionThreads"
						class="form-control col-md-2">
				</div>
				<div class="form-inline">
					<label for="reportCount" class="col-md-6">Max survey id's to use:</label> <input type="number" min="1" max="500" value="50" step="10" id="reportCount" name="reportCount" ng-model="params.reportCount"
						class="form-control col-md-2">
				</div>
			</div>

			<div class="row form-group">
				<button class="btn btn-primary" ng-click="getReports()">Test GET Reports</button>
			</div>

			<div class="row results" id="get-reports-results">
				<span ng-if="reports.reportCount">Number of reports read: {{reports.reportCount}}</span><br>
				<span ng-if="reports.reportCount">Number of reports on server: {{reports.totalReports}}</span><br>
				<span ng-if="reports.reportCount">Measured time: {{reports.duration}} ms</span><br>
				<span ng-if="reports.reportCount">Average: {{reports.duration/reports.reportCount | number}} ms/report, or <b>{{reports.rate | number}}</b> reports/hour</span><br>
			</div>
			<div class="row hide" id="get-reports-spinner" style="margin-top: -20px;">
				<asset:image src="spinner.gif"></asset:image>
			</div>
			
			<div class="row form-group">
				<button class="btn btn-primary" ng-click="getReportModels()">Test GET Reports Models</button>
			</div>
			
			<div class="row results" id="get-report-models-results">
				<span ng-if="reportModels.reportCount">Number of reports read: {{reportModels.reportCount}}</span><br>
				<span ng-if="reportModels.reportCount">Measured time: {{reportModels.duration}} ms</span><br>
				<span ng-if="reportModels.reportCount">Average: {{reportModels.duration/reportModels.reportCount | number}} ms/report, or <b>{{reportModels.rate | number}}</b> reports/hour</span><br>
			</div>
			<div class="row hide" id="get-report-models-spinner" style="margin-top: -20px;">
				<asset:image src="spinner.gif"></asset:image>
			</div>
			
			<div class="row form-group">
				<button class="btn btn-primary" ng-click="getReportQuestionModels()">Test GET Reports Models with Questions</button>
			</div>

			<div class="row results" id="get-report-models-questions-results">
				<span ng-if="reportQuestionModels.reportCount">Number of reports read: {{reportQuestionModels.reportCount}}</span><br>
				<span ng-if="reportQuestionModels.reportCount">Measured time: {{reportQuestionModels.duration}} ms</span><br>
				<span ng-if="reportQuestionModels.reportCount">Average: {{reportQuestionModels.duration/reportQuestionModels.reportCount | number}} ms/report, or <b>{{reportQuestionModels.rate | number}}</b> reports/hour</span><br>
			</div>
			<div class="row hide" id="get-report-models-questions-spinner" style="margin-top: -20px;">
				<asset:image src="spinner.gif"></asset:image>
			</div>
		</div>
		
		<div class="col-lg-6" id="surveysTesting">
			<div class="alert alert-warning">
				<b>TEST Surveys</b> (GET surveys, POST surveys)
			</div>

			<div class="row form-group form-inline">
				<label for="surveyThreads" class="col-md-6">Survey threads:</label> <input type="number" min="1" max="20" value="1" id="surveyThreads" name="surveyThreads" class="form-control col-md-2" ng-model="params.surveyThreads">

				<label for="surveyCount" class="col-md-6">Number of surveys to POST:</label> <input type="number" min="1" max="5000" value="100" step="50" id="surveyCount" name="surveyCount" ng-model="params.surveyCount"
					class="form-control col-md-2">
			</div>
			<div class="row form-group">
				<button class="btn btn-primary" ng-click="getSurveys()">Test GET Surveys</button>
			</div>

			<div class="row results" id="get-surveys-results">
				<span ng-if="surveysRead.surveyCount">Number of surveys read: {{surveysRead.surveyCount}}</span><br>
				<span ng-if="surveysRead.surveyCount">Number of surveys on server: {{surveysRead.totalSurveys}}</span><br>
				<span ng-if="surveysRead.surveyCount">Measured time: {{surveysRead.duration}} ms</span><br>
				<span ng-if="surveysRead.surveyCount">Average: {{surveysRead.duration/surveysRead.surveyCount | number}} ms/survey, or <b>{{surveysRead.rate | number}}</b> surveys/hour</span><br>
			</div>
			<div class="hide" id="get-surveys-spinner" style="margin-top: -20px;">
				<asset:image src="spinner.gif"></asset:image>
			</div>

			<div class="row form-group">
				<button class="btn btn-primary" ng-click="postSurveys()">Test POST Surveys</button>
			</div>

			<div class="row results" id="post-surveys-results">
				<span ng-if="surveysPosted.surveyCount">Number of surveys saved: {{surveysPosted.surveyCount}}</span><br>
				<span ng-if="surveysPosted.errorSurveys > 0" class="text-danger">Number of surveys with errors: {{surveysPosted.errorSurveys}}</span><br>
				<span ng-if="surveysPosted.duration">Measured time: {{surveysPosted.duration}} ms</span><br>
				<span ng-if="surveysPosted.rate">Average: {{surveysPosted.duration/surveysPosted.surveyCount | number}} ms/survey, or <b>{{surveysPosted.rate | number}}</b> surveys/hour</span><br>
			</div>
			<div class="hide" id="post-surveys-spinner" style="margin-top: -20px;">
				<asset:image src="spinner.gif"></asset:image>
			</div>
		</div>
	</ng-form>
</body>
</html>