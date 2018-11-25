<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firmDocument.entity', default: 'FirmDocument List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'FirmDocument List')}" />
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
            <el:validatableForm name="firmDocumentForm" controller="firmDocument" action="update">
                <el:hiddenField name="id" value="${firmDocument?.id}" />
                <g:render template="/firmDocument/form" model="[firmDocument:firmDocument]"/>
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>