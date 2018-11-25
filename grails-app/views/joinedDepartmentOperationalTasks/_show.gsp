<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'JoinedDepartmentOperationalTasks List')}" />
    <title>${title}</title>
</head>
<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${joinedDepartmentOperationalTasks?.operationalTask}" type="OperationalTask" label="${message(code:'joinedDepartmentOperationalTasks.operationalTask.label',default:'operationalTask')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'joinedDepartmentOperationalTasks',action:'list')}'"/>
</div>
</body>
</html>