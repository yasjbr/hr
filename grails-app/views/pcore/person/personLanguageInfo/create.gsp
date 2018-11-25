<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personLanguageInfo.entity', default: 'PersonLanguageInfo List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PersonLanguageInfo List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personLanguageInfo',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="personLanguageInfoForm" controller="personLanguageInfo" action="save">
                <g:render template="/pcore/person/personLanguageInfo/form" model="[personLanguageInfo:personLanguageInfo]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
