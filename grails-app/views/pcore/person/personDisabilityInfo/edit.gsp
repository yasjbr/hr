<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personDisabilityInfo.entity', default: 'PersonDisabilityInfo List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'PersonDisabilityInfo List')}" />
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
            <el:validatableForm name="personDisabilityInfoForm" controller="personDisabilityInfo" action="update">
                <el:hiddenField name="id" value="${personDisabilityInfo?.id}" />
                <g:render template="/pcore/person/personDisabilityInfo/form" model="[personDisabilityInfo:personDisabilityInfo]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>