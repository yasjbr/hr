<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusList.entity', default: 'MaritalStatusList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'MaritalStatusList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'maritalStatusList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="maritalStatusListForm" controller="maritalStatusList" action="update">
                <g:render template="/maritalStatusList/form" model="[maritalStatusList:maritalStatusList]"/>
                <el:hiddenField name="encodedId" value="${maritalStatusList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true" withPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>