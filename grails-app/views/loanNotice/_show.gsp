
<lay:showWidget size="12" title="${title}">

    <lay:showElement value="${loanNotice?.requestedJob}" type="String" label="${message(code:'loanNotice.requestedJob.label',default:'requestedJob')}" />
    <lay:showElement value="${loanNotice?.jobTitle}" type="String" label="${message(code:'loanNotice.jobTitle.label',default:'requestedJobTitle')}" />
    <lay:showElement value="${loanNotice?.transientData?.requesterOrganizationDTO}" type="String" label="${message(code:'loanNotice.requesterOrganizationId.label',default:'requestedFromOrganizationId')}" />
    <lay:showElement value="${loanNotice?.numberOfPositions}" type="Short" label="${message(code:'loanNotice.numberOfPositions.label',default:'numberOfPositions')}" />
    <lay:showElement value="${loanNotice?.fromDate}" type="ZonedDate" label="${message(code:'loanNotice.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${loanNotice?.toDate}" type="ZonedDate" label="${message(code:'loanNotice.toDate.label',default:'toDate')}" />
    <lay:showElement value="${loanNotice?.periodInMonths}" type="Short" label="${message(code:'loanNotice.periodInMonths.label',default:'periodInMonths')}" />
    <lay:showElement value="${loanNotice?.loanNoticeStatus}" type="enum" messagePrefix="EnumLoanNoticeStatus" label="${message(code:'loanNotice.loanNoticeStatus.label',default:'loanNoticeStatus')}" />

    <g:if test="${loanNotice?.orderNo}">
        <lay:showElement value="${loanNotice?.orderNo}" type="String" label="${message(code:'loanNotice.orderNo.label',default:'orderNo')}" />
    </g:if>
    <g:if test="${loanNotice?.orderDate}">
        <lay:showElement value="${loanNotice?.orderDate}" type="ZonedDate" label="${message(code:'loanNotice.orderDate.label',default:'orderDate')}" />
    </g:if>

    <lay:showElement value="${loanNotice?.description}" type="String" label="${message(code:'loanNotice.description.label',default:'description')}" />


</lay:showWidget>
<el:row />

<div class="clearfix form-actions text-center">
    <g:if test="${loanNotice?.loanNoticeStatus == ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus.UNDER_NOMINATION}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'loanNotice', action: 'edit', params: [encodedId: loanNotice?.encodedId])}'"/>
        <btn:button color="approve" messageCode="loanNotice.endNomination.label" icon="icon-ok" size="bigger" class="width-135"
                onClick="window.location.href='${createLink(controller: 'loanNotice', action: 'endNomination', params: [encodedId: loanNotice?.encodedId])}'"/>
    </g:if>
    <g:if test="${loanNotice?.loanNoticeStatus == ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus.DONE_NOMINATION}">
        <btn:button color="approve" messageCode="loanNotice.closeNomination.label" icon="icon-ok" size="bigger" class="width-135"
                onClick="window.location.href='${createLink(controller: 'loanNotice', action: 'closeNomination', params: [encodedId: loanNotice?.encodedId])}'"/>
    </g:if>
    <btn:backButton />
</div>