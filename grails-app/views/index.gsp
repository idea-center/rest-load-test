<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>IDEA REST SERVER Load Test Application</title>
	</head>
	<body>
		<div class="container" id="page-body" role="main">
			<div class="header well"><h4>IDEA REST SERVER Load Test Application</h4></div>
			
			<div class="row col-lg-6" id="reportsTesting">
				<div class="row">
					<g:formRemote class="form-inline" name="getReportsForm" id="getReportsForm" url="[controller: 'getReports', action: 'loadTestExistingReports']" method="GET" update="get-reports-results">
						<div class="form-group">
							<label for="hostName">Host:</label>
							<input type="text" id="hostName" name="host" class="form-control" placeholder="default: localhost">
							<label for="portNumber">Port:</label>
							<input type="text" id="portNumber" name="port" class="form-control" placeholder="default: 8091">
						</div>
						<div class="row form-group" style="display: block;">
							<label for="reportThreads" class="col-md-6">Report model threads:</label>
							<input type="number" min="1" max="20" value="1" id="reportThreads" name="reportThreads" class="form-control col-md-2">
						</div>
						<div class="row form-group" style="display: block;">
							<label for="questionThreads" class="col-md-6">Question model threads:</label>
							<input type="number" min="1" max="20" value="1" id="questionThreads" name="questionThreads" class="form-control col-md-2">
						</div>
						<div class="row form-group" style="display: block;">
							<label for="reportCount" class="col-md-6">Max survey id's to use:</label>
							<input type="number" min="1" max="500" value="50" id="reportCount" name="reportCount" class="form-control col-md-2">
						</div>
						<div class="row form-group" style="display: block;">
							<input type="submit" class="btn btn-primary" value="Test GET Reports" id="test-get-reports"/>
						</div>
					</g:formRemote>
				</div>
				<div class="row results" id="get-reports-results"></div>
				<div class="hide" id="get-reports-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
				
				<div class="row">
					<g:formRemote class="form-inline" name="getReportModelsForm" id="getReportModelsForm" url="[controller: 'getReports', action: 'loadTestExistingReportModels']" method="GET" update="get-report-models-results">
						<div class="form-group">
							<input type="hidden" id="hostModels" name="host" value="localhost">
							<input type="hidden" id="portModels" name="port" value="8091">
							<input type="hidden" id="reportThreads1" name="reportThreads" value="1">
							<input type="hidden" id="questionThreads1" name="questionThreads" value="1">
							<input type="hidden" id="reportCount1" name="reportCount" value="50">
							<input type="submit" class="btn btn-primary" value="Test GET Report Models" id="test-get-report-models"/>
						</div>
					</g:formRemote>
				</div>
				<div class="row results" id="get-report-models-results"></div>
				<div class="hide" id="get-report-models-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
				
				<div class="row">
					<g:formRemote class="form-inline" name="getReportModelsQuestionsForm" id="getReportModelsQuestionsForm" url="[controller: 'getReports', action: 'loadTestExistingReportModelsAndQuestions']" 
									method="GET" update="get-report-models-questions-results">
						<div class="form-group">
							<input type="hidden" id="hostQuestions" name="host" value="localhost">
							<input type="hidden" id="portQuestions" name="port" value="8091">
							<input type="hidden" id="reportThreads2" name="reportThreads" value="1">
							<input type="hidden" id="questionThreads2" name="questionThreads" value="1">
							<input type="hidden" id="reportCount2" name="reportCount" value="50">
							<input type="submit" class="btn btn-primary" value="Test GET Report Models with Questions" id="test-get-report-models-questions"/>
						</div>
					</g:formRemote>
				</div>
				<div class="row results" id="get-report-models-questions-results"></div>
				<div class="hide" id="get-report-models-questions-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
			</div>
			
			<div class="row col-lg-6" id="surveysTesting">
				<div class="row">
					<g:formRemote class="form-inline" name="getSurveysForm" id="getSurveysForm" url="[controller: 'postSurveys', action: 'loadTestExistingSurveys']" method="GET" update="get-surveys-results">
						<div class="form-group">
							<label for="hostName">Host:</label>
							<input type="text" id="hostName1" name="host" class="form-control" placeholder="default: localhost">
							<label for="portNumber">Port:</label>
							<input type="text" id="portNumber1" name="port" class="form-control" placeholder="default: 8091">
						</div>
						<div class="row form-group" style="display: block;">
							<label for="surveyThreads" class="col-md-6">Survey threads:</label>
							<input type="number" min="1" max="20" value="1" id="surveyThreads" name="surveyThreads" class="form-control col-md-2">
						</div>
						<div class="row form-group" style="display: block;">
							<label for="surveyCount" class="col-md-6">Number of surveys to POST:</label>
							<input type="number" min="1" max="5000" value="100" id="surveyCount" name="surveyCount" class="form-control col-md-2">
						</div>
						<div class="row form-group" style="display: block;">
							<input type="submit" class="btn btn-primary" value="Test GET Surveys" id="test-get-surveys"/>
						</div>
					</g:formRemote>
				</div>
				<div class="row results" id="get-surveys-results"></div>
				<div class="hide" id="get-surveys-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
				
				<div class="row">
					<g:formRemote class="form-inline" name="postSurveysForm" id="postSurveysForm" url="[controller: 'postSurveys', action: 'loadTestPostSurveys']" method="POST" update="post-surveys-results">
						<div class="form-group">
							<input type="hidden" id="hostName2" name="host" value="localhost">
							<input type="hidden" id="portNumber2" name="port" value="8091">
							<input type="hidden" id="surveyThreads1" name="surveyThreads" value="1">
							<input type="hidden" id="surveyCount1" name="surveyCount" value="100">
							<input type="submit" class="btn btn-primary" value="Test POST Surveys" id="test-post-surveys"/>
						</div>
					</g:formRemote>
				</div>
				<div class="row results" id="post-surveys-results"></div>
				<div class="hide" id="post-surveys-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
			</div>
		</div>
	</body>
</html>
