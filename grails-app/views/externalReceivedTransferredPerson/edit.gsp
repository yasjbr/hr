<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ExternalReceivedTransferredPerson List')}" />
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
            <el:validatableForm name="externalReceivedTransferredPersonForm" controller="externalReceivedTransferredPerson" action="update">
                <g:render template="/externalReceivedTransferredPerson/form" model="[externalReceivedTransferredPerson:externalReceivedTransferredPerson]"/>
                <el:hiddenField name="id" value="${externalReceivedTransferredPerson?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>