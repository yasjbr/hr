<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationList.entity', default: 'EvaluationList List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EvaluationList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="evaluationListForm" controller="evaluationList" action="update">
                <g:render template="/evaluationList/form" model="[evaluationList:evaluationList]"/>
                <el:hiddenField name="encodedId" value="${evaluationList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>