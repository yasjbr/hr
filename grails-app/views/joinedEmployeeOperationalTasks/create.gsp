<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'joinedEmployeeOperationalTasks.entity', default: 'JoinedEmployeeOperationalTasks List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'JoinedEmployeeOperationalTasks List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'joinedEmployeeOperationalTasks',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="joinedEmployeeOperationalTasksForm" callLoadingFunction="performPostActionWithEncodedId" controller="joinedEmployeeOperationalTasks" action="save">
                <g:render template="/joinedEmployeeOperationalTasks/form" model="[joinedEmployeeOperationalTasks:joinedEmployeeOperationalTasks]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
