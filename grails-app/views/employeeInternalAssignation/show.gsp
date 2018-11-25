<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeInternalAssignation.entity', default: 'EmployeeInternalAssignation List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EmployeeInternalAssignation List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'employeeInternalAssignation',action:'list')}'"/>
    </div></div>
</div>
<g:render template="/employeeInternalAssignation/show" model="[title:title,employeeInternalAssignation:employeeInternalAssignation]"/>
<el:row />

</body>
</html>