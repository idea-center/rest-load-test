<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>IDEA REST SERVER Load Test Application</title>
	</head>
	<body>
		<div class="container" id="page-body" role="main">
			<div class="header well"><h4>IDEA REST SERVER Load Test Application</h4></div>
			
			<div class="row">
				<g:form class="form form-horizontal">
					<div class="row" style="margin-top: 0;">
						<div class="col-md-3">
							<label for="hostName" class="control-label">Host:</label>
							<input type="text" id="hostName" name="host" class="form-control" placeholder="default: localhost">
						</div>
						<div class="col-md-2">
							<label for="portNumber" class="control-label">Port:</label>
							<input type="text" id="portNumber" name="port" class="form-control" placeholder="default: 8091">
						</div>
						<div class="col-md-2">
							<label for="appName" class="control-label">App name:</label>
							<input type="text" id="appName" name="appName" class="form-control" placeholder="enter app name">
						</div>
						<div class="col-md-5">
							<label for="appKey" class="control-label">App key:</label>
							<input type="text" id="appKey" name="appKey" class="form-control" placeholder="enter app key">
						</div>
					</div>
					
					<div class="col-lg-6" id="reportsTesting">
						<div class="alert alert-success"><b>TEST Reports</b> (GET report metadata, report models)</div>
						
						<div class="row form-group ">
							<div class="form-inline">
								<label for="reportThreads" class="col-md-6">Report model threads:</label>
								<input type="number" min="1" max="20" value="1" id="reportThreads" name="reportThreads" class="form-control col-md-2">
							</div>
							<div class="form-inline">
								<label for="questionThreads" class="col-md-6">Question model threads:</label>
								<input type="number" min="1" max="20" value="1" id="questionThreads" name="questionThreads" class="form-control col-md-2">
							</div>
							<div class="form-inline">
								<label for="reportCount" class="col-md-6">Max survey id's to use:</label>
								<input type="number" min="1" max="500" value="50" id="reportCount" name="reportCount" class="form-control col-md-2">
							</div>
						</div>
						
						<div class="row form-group">
							<g:submitToRemote class="btn btn-primary submit" value="Test GET Reports" id="test-get-reports" url="[controller: 'getReports', action: 'loadTestExistingReports']" method="GET" update="get-reports-results" />
						</div>
						<div class="row hide" id="get-reports-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
						<div class="row results" id="get-reports-results"></div>
						
						<div class="row form-group">
							<g:submitToRemote class="btn btn-primary submit" value="Test GET Report Models" id="test-get-report-models" url="[controller: 'getReports', action: 'loadTestExistingReportModels']" method="GET" 
											update="get-report-models-results" />
						</div>
						<div class="row hide" id="get-report-models-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
						<div class="row results" id="get-report-models-results"></div>
						
						<div class="row form-group">
							<g:submitToRemote class="btn btn-primary submit" value="Test GET Report Models with Questions" id="test-get-report-models-questions" url="[controller: 'getReports', action: 'loadTestExistingReportModelsAndQuestions']" 
											method="GET" update="get-report-models-questions-results" />
						</div>
						<div class="row hide" id="get-report-models-questions-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
						<div class="row results" id="get-report-models-questions-results"></div>
					</div>
					
					<div class="col-lg-6" id="surveysTesting">
						<div class="alert alert-warning"><b>TEST Surveys</b> (GET surveys, POST surveys)</div>
						
						<div class="row form-group form-inline">
							<label for="surveyThreads" class="col-md-6">Survey threads:</label>
							<input type="number" min="1" max="20" value="1" id="surveyThreads" name="surveyThreads" class="form-control col-md-2">

							<label for="surveyCount" class="col-md-6">Number of surveys to POST:</label>
							<input type="number" min="1" max="5000" value="100" id="surveyCount" name="surveyCount" class="form-control col-md-2">
						</div>
						<div class="row form-group">
							<g:submitToRemote class="btn btn-primary submit" value="Test GET Surveys" id="test-get-surveys" url="[controller: 'postSurveys', action: 'loadTestExistingSurveys']" method="GET" 
									update="get-surveys-results" />
						</div>
						<div class="row results" id="get-surveys-results"></div>
						<div class="hide" id="get-surveys-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
						
						<div class="row form-group">
							<g:submitToRemote class="btn btn-primary submit" value="Test POST Surveys" id="test-post-surveys" url="[controller: 'postSurveys', action: 'loadTestPostSurveys']" method="POST" 
									update="post-surveys-results" />
						</div>
						<div class="row results" id="post-surveys-results"></div>
						<div class="hide" id="post-surveys-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
					</div>
				</g:form>
			</div>
			
			
		</div>
	</body>
</html>
