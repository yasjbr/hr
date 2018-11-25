<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ExternalTransferRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton style="margin-right: 10px;" onClick="window.location.href='${createLink(controller:'externalTransferRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="externalTransferRequestForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="externalTransferRequest" action="save">
                <g:render template="/externalTransferRequest/form" model="[externalTransferRequest:externalTransferRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
