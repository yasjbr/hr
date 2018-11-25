<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'InternalTransferRequest List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'internalTransferRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="internalTransferRequestForm" controller="internalTransferRequest" action="update">
                <g:render template="/internalTransferRequest/form" model="[internalTransferRequest:internalTransferRequest]"/>
                <el:hiddenField name="id" value="${internalTransferRequest?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>