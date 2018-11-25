<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LoanListPersonNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'loanListPersonNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${loanListPersonNote?.loanListPerson}" type="LoanListPerson" label="${message(code:'loanListPersonNote.loanListPerson.label',default:'loanListPerson')}" />
    <lay:showElement value="${loanListPersonNote?.note}" type="String" label="${message(code:'loanListPersonNote.note.label',default:'note')}" />
    <lay:showElement value="${loanListPersonNote?.noteDate}" type="ZonedDateTime" label="${message(code:'loanListPersonNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${loanListPersonNote?.orderNo}" type="String" label="${message(code:'loanListPersonNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>
<el:row />

</body>
</html>