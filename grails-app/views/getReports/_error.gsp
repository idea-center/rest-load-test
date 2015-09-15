<span class="text-danger">Problem calling this end-point. Status was ${status}.</span>

<g:if test="${test == 'reports'}">
	<g:javascript>
		$('#test-get-reports').removeAttr('disabled');
		$('#get-reports-spinner').addClass('hide');
	</g:javascript>
</g:if>

<g:if test="${test == 'reportModels'}">
	<g:javascript>
		$('#test-get-report-models').removeAttr('disabled');
		$('#get-report-models-spinner').addClass('hide');
	</g:javascript>
</g:if>

<g:if test="${test == 'reportModelQuestions'}">
	<g:javascript>
		$('#test-get-report-models-questions').removeAttr('disabled');
		$('#get-report-models-questions-spinner').addClass('hide');
	</g:javascript>
</g:if>