<el:row/><br/>
<g:render template="/employee/employeeShowWrapper" model="[employee: loanNoticeReplayRequest?.employee]"/>
<el:row/>
<el:row/>
<el:row/>
<lay:showWidget size="12" title="${message(code: 'request.info.label')}">
    <g:render template="/request/wrapperRequestShow" model="[request: loanNoticeReplayRequest]"/>

    <lay:showElement value="${loanNoticeReplayRequest?.transientData?.requestedByOrganizationDTO}"  type="string"
                     label="${message(code:'loanNoticeReplayRequest.transientData.requestedByOrganizationDTO.label',default:'fromDate')}" />

    <lay:showElement value="${loanNoticeReplayRequest?.fromDate}" type="ZonedDate"
                     label="${message(code:'loanNoticeReplayRequest.fromDate.label',default:'fromDate')}" />

    <lay:showElement value="${loanNoticeReplayRequest?.toDate}" type="ZonedDate"
                     label="${message(code:'loanNoticeReplayRequest.toDate.label',default:'toDate')}" />


    <lay:showElement value="${loanNoticeReplayRequest?.periodInMonths}" type="short"
                     label="${message(code:'loanNoticeReplayRequest.periodInMonths.label',default:'periodInMonth')}" />

    <g:if test="${loanNoticeReplayRequest?.effectiveDate}">
        <lay:showElement value="${loanNoticeReplayRequest?.effectiveDate}" type="ZonedDate"
                         label="${message(code:'loanNoticeReplayRequest.effectiveDate.label',default:'effectiveDate')}" />
    </g:if>

</lay:showWidget>
<el:row/>
<el:row/>
<g:render template="/request/wrapperManagerialOrderShow" model="[request: loanNoticeReplayRequest, colSize: 12]"/>
<el:row/>
<el:row/>
<g:render template="/request/wrapperShow" model="[request: loanNoticeReplayRequest]"/>
<el:row/>