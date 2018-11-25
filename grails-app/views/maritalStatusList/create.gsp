<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusList.entity', default: 'MaritalStatusList List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'MaritalStatusList List')}" />
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
            <el:validatableResetForm name="maritalStatusListForm" callLoadingFunction="performPostActionWithEncodedId" controller="maritalStatusList" action="save">
                <g:render template="/maritalStatusList/form" model="[maritalStatusList:maritalStatusList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="back"  goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
