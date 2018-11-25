<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ExternalReceivedTransferredPerson List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'externalReceivedTransferredPerson',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="externalReceivedTransferredPersonForm" callLoadingFunction="performPostActionWithEncodedId" controller="externalReceivedTransferredPerson" action="save">
                <g:render template="/externalReceivedTransferredPerson/form" model="[externalReceivedTransferredPerson:externalReceivedTransferredPerson]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
