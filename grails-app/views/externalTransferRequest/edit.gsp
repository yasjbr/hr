<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ExternalTransferRequest List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'externalTransferRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="externalTransferRequestForm" controller="externalTransferRequest" action="update">
                <g:render template="/externalTransferRequest/form" model="[externalTransferRequest:externalTransferRequest]"/>
                <el:hiddenField name="id" value="${externalTransferRequest?.id}" />
                <el:formButton isSubmit="true" functionName="save"  withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>