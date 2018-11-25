<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'SuspensionExtensionList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionExtensionList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="suspensionExtensionListForm" controller="suspensionExtensionList" action="update">
                <g:render template="/suspensionExtensionList/form" model="[suspensionExtensionList:suspensionExtensionList]"/>
                <el:hiddenField name="encodedId" value="${suspensionExtensionList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>