
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${secondmentNotice?.description}" type="String" label="${message(code:'secondmentNotice.description.label',default:'description')}" />
    <lay:showElement value="${secondmentNotice?.fromDate}" type="ZonedDateTime" label="${message(code:'secondmentNotice.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${secondmentNotice?.jobTitle}" type="String" label="${message(code:'secondmentNotice.jobTitle.label',default:'jobTitle')}" />
    <lay:showElement value="${secondmentNotice?.militaryRank}" type="MilitaryRank" label="${message(code:'secondmentNotice.militaryRank.label',default:'militaryRank')}" />
    <lay:showElement value="${secondmentNotice?.orderNo}" type="String" label="${message(code:'secondmentNotice.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${secondmentNotice?.periodInMonths}" type="Short" label="${message(code:'secondmentNotice.periodInMonths.label',default:'periodInMonths')}" />
    <lay:showElement value="${secondmentNotice?.requesterOrganizationId}" type="Long" label="${message(code:'secondmentNotice.requesterOrganizationId.label',default:'requesterOrganizationId')}" />
    <lay:showElement value="${secondmentNotice?.toDate}" type="ZonedDateTime" label="${message(code:'secondmentNotice.toDate.label',default:'toDate')}" />
</lay:showWidget>
<el:row />
