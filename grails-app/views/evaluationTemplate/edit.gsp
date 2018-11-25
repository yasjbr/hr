<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'evaluationTemplate.entity', default: 'EvaluationTemplate List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'EvaluationTemplate List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller:'evaluationTemplate',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="evaluationTemplateForm" controller="evaluationTemplate" action="update">
                <el:hiddenField name="id" value="${evaluationTemplate?.id}"/>
                <g:render template="form" model="[evaluationTemplate:evaluationTemplate, militaryRanks: militaryRanks, jobCategories: jobCategories]"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>