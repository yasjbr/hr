<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'firmSetting.entity', default: 'FirmSetting List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'FirmSetting List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'firmSetting', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="firmSettingForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="firmSetting" action="save">
                <g:render template="/firmSetting/form" model="[firmSetting: firmSetting]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'firmSetting',action:'list')}'"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
