<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'generalList.entity', default: 'SuspensionExtensionList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'SuspensionExtensionList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'generalList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="generalListForm" controller="generalList" action="update">
                <g:render template="/generalList/form" model="[generalList:generalList]"/>
                <el:hiddenField name="encodedId" value="${generalList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel"  goToPreviousLink="true" withPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>