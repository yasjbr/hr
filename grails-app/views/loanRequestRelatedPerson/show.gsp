<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LoanRequestRelatedPerson List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'loanRequestRelatedPerson',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${loanRequestRelatedPerson?.effectiveDate}" type="ZonedDateTime" label="${message(code:'loanRequestRelatedPerson.effectiveDate.label',default:'effectiveDate')}" />
    <lay:showElement value="${loanRequestRelatedPerson?.firm}" type="Firm" label="${message(code:'loanRequestRelatedPerson.firm.label',default:'firm')}" />
    <lay:showElement value="${loanRequestRelatedPerson?.loanRequest}" type="LoanRequest" label="${message(code:'loanRequestRelatedPerson.loanRequest.label',default:'loanRequest')}" />
    <lay:showElement value="${loanRequestRelatedPerson?.recordSource}" type="enum" label="${message(code:'loanRequestRelatedPerson.recordSource.label',default:'recordSource')}" messagePrefix="EnumPersonSource" />
    <lay:showElement value="${loanRequestRelatedPerson?.requestedPersonId}" type="Long" label="${message(code:'loanRequestRelatedPerson.requestedPersonId.label',default:'requestedPersonId')}" />
</lay:showWidget>
<el:row />

</body>
</html>