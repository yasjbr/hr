<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeExternalAssignation.entity', default: 'EmployeeInternalAssignation List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EmployeeInternalAssignation List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'employeeExternalAssignation',action:'list')}'"/>
    </div></div>
</div>
<g:render template="/employeeExternalAssignation/show" model="[title:title,employeeExternalAssignation:employeeExternalAssignation]"/>
<el:row />

</body>
</html>