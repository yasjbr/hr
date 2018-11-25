<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PersonDisabilityInfo List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'personDisabilityInfo',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="personDisabilityInfoForm" controller="personDisabilityInfo" action="save">
                <g:render template="/pcore/person/personDisabilityInfo/form" model="[personDisabilityInfo:personDisabilityInfo]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
