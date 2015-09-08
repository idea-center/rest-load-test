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
				<g:formRemote class="form-inline" name="getReportsForm" id="getReportsForm" url="[controller: 'getReports', action: 'loadTestExistingReports']" method="GET" update="get-reports-results">
					<div class="form-group">
						<label for="hostName">Host:</label>
						<input type="text" id="hostName" name="host" class="form-control" placeholder="default: localhost">
						<label for="portNumber">Port:</label>
						<input type="text" id="portNumber" name="port" class="form-control" placeholder="default: 8091">
					</div>
					<div class="row form-group" style="display: block;">
						<label for="reportThreads" class="col-md-3">Report model threads:</label>
						<input type="number" min="1" max="20" value="1" id="reportThreads" name="reportThreads" class="form-control col-md-2">
					</div>
					<div class="row form-group" style="display: block;">
						<label for="questionThreads" class="col-md-3">Question model threads:</label>
						<input type="number" min="1" max="20" value="1" id="questionThreads" name="questionThreads" class="form-control col-md-2">
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
						<input type="hidden" id="hostModels" name="host">
						<input type="hidden" id="portModels" name="port">
						<input type="hidden" id="reportThreads1" name="reportThreads">
						<input type="hidden" id="questionThreads1" name="questionThreads">
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
						<input type="hidden" id="hostQuestions" name="host">
						<input type="hidden" id="portQuestions" name="port">
						<input type="hidden" id="reportThreads2" name="reportThreads">
						<input type="hidden" id="questionThreads2" name="questionThreads">
						<input type="submit" class="btn btn-primary" value="Test GET Report Models with Questions" id="test-get-report-models-questions"/>
					</div>
				</g:formRemote>
			</div>
			<div class="row results" id="get-report-models-questions-results"></div>
			<div class="hide" id="get-report-models-questions-spinner" style="margin-top: -20px;"><asset:image src="spinner.gif"></asset:image></div>
		</div>
	</body>
</html>
