<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanListPerson.entity', default: 'LoanListPerson List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LoanListPerson List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'loanListPerson',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${loanListPerson?.description}" type="String" label="${message(code:'loanListPerson.description.label',default:'description')}" />
    <lay:showElement value="${loanListPerson?.effectiveDate}" type="ZonedDateTime" label="${message(code:'loanListPerson.effectiveDate.label',default:'effectiveDate')}" />
    <lay:showElement value="${loanListPerson?.firm}" type="Firm" label="${message(code:'loanListPerson.firm.label',default:'firm')}" />
    <lay:showElement value="${loanListPerson?.fromDate}" type="ZonedDateTime" label="${message(code:'loanListPerson.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${loanListPerson?.isEmploymentProfileProvided}" type="Boolean" label="${message(code:'loanListPerson.isEmploymentProfileProvided.label',default:'isEmploymentProfileProvided')}" />
    <lay:showElement value="${loanListPerson?.loanList}" type="LoanList" label="${message(code:'loanListPerson.loanList.label',default:'loanList')}" />
    <lay:showElement value="${loanListPerson?.loanListPersonNotes}" type="Set" label="${message(code:'loanListPerson.loanListPersonNotes.label',default:'loanListPersonNotes')}" />
    <lay:showElement value="${loanListPerson?.loanRequest}" type="LoanRequest" label="${message(code:'loanListPerson.loanRequest.label',default:'loanRequest')}" />
    <lay:showElement value="${loanListPerson?.periodInMonths}" type="Short" label="${message(code:'loanListPerson.periodInMonths.label',default:'periodInMonths')}" />
    <lay:showElement value="${loanListPerson?.recordStatus}" type="enum" label="${message(code:'loanListPerson.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${loanListPerson?.requestedFromOrganizationId}" type="Long" label="${message(code:'loanListPerson.requestedFromOrganizationId.label',default:'requestedFromOrganizationId')}" />
    <lay:showElement value="${loanListPerson?.requestedPersonId}" type="Long" label="${message(code:'loanListPerson.requestedPersonId.label',default:'requestedPersonId')}" />
    <lay:showElement value="${loanListPerson?.toDate}" type="ZonedDateTime" label="${message(code:'loanListPerson.toDate.label',default:'toDate')}" />
    <lay:showElement value="${loanListPerson?.toDepartment}" type="Department" label="${message(code:'loanListPerson.toDepartment.label',default:'toDepartment')}" />
</lay:showWidget>
<el:row />

</body>
</html>