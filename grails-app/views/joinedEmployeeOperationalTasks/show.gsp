<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'joinedEmployeeOperationalTasks.entity', default: 'EmploymentRecord List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'EmploymentRecord List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'joinedEmployeeOperationalTasks',action:'list')}'"/>
    </div></div>
</div>
<el:row />
<g:render template="/joinedEmployeeOperationalTasks/show" model="[joinedEmployeeOperationalTasks:joinedEmployeeOperationalTasks]"/>

</body>
</html>