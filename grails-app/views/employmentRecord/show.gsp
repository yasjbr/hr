<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employmentRecord.entity', default: 'EmploymentRecord List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EmploymentRecord List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'employmentRecord',action:'list')}'"/>
    </div></div>
</div>
<g:render template="/employmentRecord/show" model="[title:title,employmentRecord:employmentRecord]"/>
<el:row />
</body>
</html>