// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require jquery
//= require_tree .
//= require_self

if (typeof jQuery !== 'undefined') {
	(function($) {
		$('#spinner').ajaxStart(function() {
			$(this).fadeIn();
		}).ajaxStop(function() {
			$(this).fadeOut();
		});
	})(jQuery);
}

$(document).ready(function()
{
	disableButtons();
	
	/*$('#appKey').change(function(){
		if ($(this).val() && $('#appName').val()) enableButtons();
		else disableButtons();
	});*/
	
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
		$('#getReportsForm').submit();
		$('#get-reports-spinner').removeClass('hide');
	});
	
	$('#test-get-report-models').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#get-report-models-results').html('');
		$('#getReportModelsForm').submit();
		$('#get-report-models-spinner').removeClass('hide');
	});
	
	$('#test-get-report-models-questions').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#get-report-models-questions-results').html('');
		$('#getReportModelsQuestionsForm').submit();
		$('#get-report-models-questions-spinner').removeClass('hide');
	});
	
	$('#reportCount').change(function(){
		$('#reportCount1').val($(this).val());
		$('#reportCount2').val($(this).val());
	});
	
	$('#test-get-surveys').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#get-surveys-results').html('');
		$('#getSurveysForm').submit();
		$('#get-surveys-spinner').removeClass('hide');
	});
	
	$('#test-post-surveys').click(function(){
		$(this).attr('disabled', 'disabled');
		$('#post-surveys-results').html('');
		$('#postSurveysForm').submit();
		$('#post-surveys-spinner').removeClass('hide');
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