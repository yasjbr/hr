<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firmSupportContactInfo.entity', default: 'FirmSupportContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'FirmSupportContactInfo List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'firmSupportContactInfo',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="firmSupportContactInfoForm" controller="firmSupportContactInfo" action="update">
                <el:hiddenField name="id" value="${firmSupportContactInfo?.id}" />
                <g:render template="/firmSupportContactInfo/form" model="[firmSupportContactInfo:firmSupportContactInfo]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>