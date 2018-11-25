<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'violationList.entity', default: 'ViolationList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ViolationList List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'violationList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="violationListForm" controller="violationList" action="update">
                <g:render template="/violationList/form" model="[violationList:violationList]"/>
                <el:hiddenField name="id" value="${violationList?.id}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>