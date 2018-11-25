<lay:showWidget size="12" title="${title}">

    <g:render template="/request/wrapperRequestShow" model="[request:loanNominatedEmployee?.loanNoticeReplayRequest,hasEmployee:true]" />


    <lay:showElement value="${loanNominatedEmployee?.loanNoticeReplayRequest?.transientData?.requestedByOrganizationDTO}"  type="string"
                     label="${message(code:'loanNominatedEmployee.loanNoticeReplayRequest.transientData.requestedByOrganizationDTO.label',default:'fromDate')}" />

    <lay:showElement value="${loanNominatedEmployee?.fromDate}" type="ZonedDate"
                     label="${message(code:'loanNominatedEmployee.fromDate.label',default:'fromDate')}" />

    <lay:showElement value="${loanNominatedEmployee?.toDate}" type="ZonedDate"
                     label="${message(code:'loanNominatedEmployee.toDate.label',default:'toDate')}" />


    <lay:showElement value="${loanNominatedEmployee?.periodInMonth}" type="short"
                     label="${message(code:'loanNominatedEmployee.periodInMonth.label',default:'periodInMonth')}" />

    <g:if test="${loanNominatedEmployee?.effectiveDate}">
        <lay:showElement value="${loanNominatedEmployee?.effectiveDate}" type="ZonedDate"
                         label="${message(code:'loanNominatedEmployee.effectiveDate.label',default:'effectiveDate')}" />
    </g:if>

    <g:if test="${loanNominatedEmployee?.orderNo}">
        <lay:showElement value="${loanNominatedEmployee?.orderNo}" type="string"
                         label="${message(code:'loanNominatedEmployee.orderNo.label',default:'orderNo')}" />
    </g:if>

    <g:if test="${loanNominatedEmployee?.orderDate}">
        <lay:showElement value="${loanNominatedEmployee?.orderDate}" type="string"
                         label="${message(code:'loanNominatedEmployee.orderDate.label',default:'orderDate')}" />
    </g:if>


    <g:render template="/request/wrapperShow" model="[request:loanNominatedEmployee?.loanNoticeReplayRequest]" />

</lay:showWidget>
<el:row />