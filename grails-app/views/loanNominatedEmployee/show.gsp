<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanNominatedEmployee.entity', default: 'LoanNominatedEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LoanNominatedEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'loanNominatedEmployee',action:'list')}'"/>
    </div></div>
</div>

<g:render template="/loanNominatedEmployee/show" model="[loanNominatedEmployee:loanNominatedEmployee,title:title]" />

</body>
</html>