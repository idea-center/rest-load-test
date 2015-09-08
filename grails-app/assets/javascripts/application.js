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
	
	$('#hostName').change(function(){
		$('#hostModels').val($(this).val());
		$('#hostQuestions').val($(this).val());
	});
	
	$('#portNumber').change(function(){
		$('#portModels').val($(this).val());
		$('#portQuestions').val($(this).val());
	});
});