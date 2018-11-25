<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'trainingClassification.entity', default: 'TrainingClassification List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'TrainingClassification List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'trainingClassification', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="trainingClassificationForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="trainingClassification" action="save">
                <g:render template="/trainingClassification/form" model="[trainingClassification: trainingClassification]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'trainingClassification', action: 'list')}'"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
