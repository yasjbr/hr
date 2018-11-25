<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'operationalTask.entity', default: 'OperationalTask List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'OperationalTask List')}" />
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
            <el:validatableResetForm name="operationalTaskForm" callLoadingFunction="performPostActionWithEncodedId" controller="operationalTask" action="save">
                <g:render template="/operationalTask/form" model="[operationalTask:operationalTask]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'operationalTask',action:'list')}'"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
