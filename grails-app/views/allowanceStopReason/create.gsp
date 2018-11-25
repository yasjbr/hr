<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'AllowanceStopReason List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'allowanceStopReason',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="allowanceStopReasonForm" callLoadingFunction="performPostActionWithEncodedId" controller="allowanceStopReason" action="save">
                <g:render template="/allowanceStopReason/form" model="[allowanceStopReason:allowanceStopReason]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
