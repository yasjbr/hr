<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationTemplate.entity', default: 'EvaluationTemplate List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EvaluationTemplate List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationTemplate',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="evaluationTemplateForm" callLoadingFunction="performPostActionWithEncodedId" controller="evaluationTemplate" action="save">
                <g:render template="/evaluationTemplate/form" model="[evaluationTemplate:evaluationTemplate, militaryRanks: militaryRanks, jobCategories: jobCategories]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'evaluationTemplate',action:'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
