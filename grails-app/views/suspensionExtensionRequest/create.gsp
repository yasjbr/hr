<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'SuspensionExtensionRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionExtensionRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="suspensionExtensionRequestForm" callLoadingFunction="performPostActionWithEncodedId" controller="suspensionExtensionRequest" action="save">
                <g:render template="/suspensionExtensionRequest/form" model="[suspensionExtensionRequest:suspensionExtensionRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
