<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'JoinedFirmOperationDocument List')}" />
    <title>${title}</title>
    <g:render template="script"/>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'joinedFirmOperationDocument',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="joinedFirmOperationDocumentForm" controller="joinedFirmOperationDocument" action="update">
                <g:render template="/joinedFirmOperationDocument/form" model="[joinedFirmOperationDocument:joinedFirmOperationDocument]"/>
                <el:hiddenField name="id" value="${joinedFirmOperationDocument?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>