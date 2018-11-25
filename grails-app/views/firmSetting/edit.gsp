<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firmSetting.entity', default: 'FirmSetting List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'FirmSetting List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'firmSetting',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="firmSettingForm" controller="firmSetting" action="update">
                <el:hiddenField name="id" value="${firmSetting?.id}" />
                <g:render template="/firmSetting/form" model="[firmSetting:firmSetting]"/>
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>