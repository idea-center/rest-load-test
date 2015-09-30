<g:if test="${duration > 0 && surveyCount > 0 && (duration/surveyCount) < 1}">
	<g:set var="decimals" value="2" />
</g:if>
<g:elseif test="${duration > 0 && surveyCount > 0 && (duration/surveyCount) < 10}">
	<g:set var="decimals" value="1" />
</g:elseif>
<g:else>
	<g:set var="decimals" value="0" />
</g:else>

<g:if test="${test == 'surveys'}">
	<span>Total number of surveys: ${totalSurveys}</span>
	<br>
	<span>Number of surveys read: ${surveyCount}</span>
	<br>
</g:if>
<g:else>
	<span>Number of surveys saved: ${surveyCount}</span>
	<br>
	<g:if test="${errorSurveys}">
		<span class="text-danger">Number of surveys with errors: ${errorSurveys}</span><br>
	</g:if>
</g:else>

<span>Measured time: ${duration} ms</span>
<br>
<g:if test="${duration > 0 && surveyCount > 0}">
	<span>Average: <g:formatNumber number="${duration/surveyCount}" type="number" minFractionDigits="${decimals}" maxFractionDigits="${decimals}" /> ms/survey, 
			or <b><g:formatNumber number="${rate}" format="###,###" /></b> surveys/hour</span>
</g:if>

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