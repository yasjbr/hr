<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'traineeList.entity', default: 'TraineeList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'TraineeList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'traineeList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="traineeListForm" controller="traineeList" action="update">
                <g:render template="/traineeList/form" model="[traineeList:traineeList]"/>
                <el:hiddenField name="encodedId" value="${traineeList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>