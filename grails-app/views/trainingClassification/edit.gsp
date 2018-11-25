<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingClassification.entity', default: 'TrainingClassification List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'TrainingClassification List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'trainingClassification',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="trainingClassificationForm" controller="trainingClassification" action="update">
                <g:render template="/trainingClassification/form" model="[trainingClassification:trainingClassification]"/>
                <el:hiddenField name="id" value="${trainingClassification?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>