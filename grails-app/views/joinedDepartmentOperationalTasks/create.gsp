<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'JoinedDepartmentOperationalTasks List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'joinedDepartmentOperationalTasks',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="joinedDepartmentOperationalTasksForm" controller="joinedDepartmentOperationalTasks" action="save">
                <g:render template="/joinedDepartmentOperationalTasks/form" model="[joinedDepartmentOperationalTasks:joinedDepartmentOperationalTasks]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
