<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionList.entity', default: 'SuspensionExtensionList List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'SuspensionExtensionList List')}" />
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
            <el:validatableResetForm name="suspensionExtensionListForm" callLoadingFunction="performPostActionWithEncodedId" controller="suspensionExtensionList" action="save">
                <g:render template="/suspensionExtensionList/form" model="[suspensionExtensionList:suspensionExtensionList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'suspensionExtensionList', action: 'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
