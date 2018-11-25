<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'contactInfo.entity', default: 'ContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ContactInfo List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'contactInfo',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="contactInfoForm" controller="contactInfo" action="update">
                <el:hiddenField name="id" value="${contactInfo?.id}" />
                <g:render template="/pcore/person/contactInfo/form" model="[contactInfo:contactInfo]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>