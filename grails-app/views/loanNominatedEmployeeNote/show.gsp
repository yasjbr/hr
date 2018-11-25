<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNominatedEmployeeNote.entity', default: 'LoanListPersonNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LoanListPersonNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'loanNominatedEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${loanNominatedEmployeeNote?.loanNominatedEmployee}" type="LoanListPerson" label="${message(code:'loanNominatedEmployeeNote.loanNominatedEmployee.label',default:'loanNominatedEmployee')}" />
    <lay:showElement value="${loanNominatedEmployeeNote?.note}" type="String" label="${message(code:'loanNominatedEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${loanNominatedEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'loanNominatedEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${loanNominatedEmployeeNote?.orderNo}" type="String" label="${message(code:'loanNominatedEmployeeNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>
<el:row />

</body>
</html>