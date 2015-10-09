// This is a manifest file that will be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
//= require jquery
//= require_tree .
//= require_self

$(document).ready(function()
{
	disableButtons();
	
	$('#appName').bind("change paste keyup", function() {
		if ($(this).val() && $('#appKey').val()) enableButtons();
		else disableButtons();
	});
	
	$('#appKey').bind("change paste keyup", function() {
		if ($(this).val() && $('#appName').val()) enableButtons();
		else disableButtons();
	});
	
	$('#test-get-reports').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#get-reports-results').html('');
		//$('#getReportsForm').submit();
		$('#get-reports-spinner').removeClass('hide');
	});
	
	$('#test-get-report-models').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#get-report-models-results').html('');
		//$('#getReportModelsForm').submit();
		$('#get-report-models-spinner').removeClass('hide');
	});
	
	$('#test-get-report-models-questions').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#get-report-models-questions-results').html('');
		//$('#getReportModelsQuestionsForm').submit();
		$('#get-report-models-questions-spinner').removeClass('hide');
	});
	
	$('#test-get-surveys').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#get-surveys-results').html('');
		//$('#getSurveysForm').submit();
		$('#get-surveys-spinner').removeClass('hide');
	});
	
	$('#test-post-surveys').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#post-surveys-results').html('');
		//$('#postSurveysForm').submit();
		$('#post-surveys-spinner').removeClass('hide');
	});
	
	$('#test-async').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#test-async-results').html('');
		//$('#postSurveysForm').submit();
		$('#test-async-spinner').removeClass('hide');
	});
});

function enableButtons()
{
	$('.submit').removeAttr('disabled');
}

function disableButtons()
{
	$('.submit').attr('disabled', true);
}