<g:if test="${duration > 0 && (duration/reportCount) < 1}">
	<g:set var="decimals" value="2" />
</g:if>
<g:elseif test="${duration > 0 && (duration/reportCount) < 10}">
	<g:set var="decimals" value="1" />
</g:elseif>
<g:else>
	<g:set var="decimals" value="0" />
</g:else>

<span>Number of reports read: ${reportCount}</span>
<br>
<g:if test="${test == 'reports'}">
	<span>Number of reports on server: ${totalReports}</span>
	<br>
</g:if>
<g:if test="${reportsWithNoData}">
	<span class="text-danger">Number of reports with no question data: ${reportsWithNoData}</span><br>
</g:if>
<span>Measured time: ${duration} ms</span>
<br>
<g:if test="${duration > 0 && reportCount > 0}">
	<span>Average: <g:formatNumber number="${duration/reportCount}" type="number" minFractionDigits="${decimals}" maxFractionDigits="${decimals}" /> ms/report, 
			or <g:formatNumber number="${rate}" format="###,###" /> reports/hour</span>
</g:if>

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