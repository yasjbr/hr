<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firmDocument.entity', default: 'FirmDocument List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'FirmDocument List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'firmDocument',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="firmDocumentForm" callLoadingFunction="performPostActionWithEncodedId" controller="firmDocument" action="save">
                <g:render template="/firmDocument/form" model="[firmDocument:firmDocument]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'firmDocument',action:'list')}'"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
