<span class="text-danger">Problem calling this end-point. Status was ${status}.</span>

<g:if test="${test == 'surveys'}">
	<g:javascript>
		$('#test-get-surveys').removeAttr('disabled');
		$('#get-surveys-spinner').addClass('hide');
	</g:javascript>
</g:if>

<g:if test="${test == 'postSurveys'}">
	<g:javascript>
		$('#test-post-surveys').removeAttr('disabled');
		$('#post-surveys-spinner').addClass('hide');
	</g:javascript>
</g:if>