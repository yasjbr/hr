<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'operationalTask.entity', default: 'OperationalTask List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'OperationalTask List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'operationalTask',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="operationalTaskForm" controller="operationalTask" action="update">
                <g:render template="/operationalTask/form" model="[operationalTask:operationalTask]"/>
                <el:hiddenField name="id" value="${operationalTask?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>