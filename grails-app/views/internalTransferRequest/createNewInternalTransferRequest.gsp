<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'InternalTransferRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton style="margin-right: 10px;" onClick="window.location.href='${createLink(controller:'internalTransferRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="internalTransferRequestForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="internalTransferRequest" action="save">
                <g:render template="/internalTransferRequest/form" model="[internalTransferRequest:internalTransferRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
