<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingRecord.entity', default: 'TrainingRecord List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'TrainingRecord List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'trainingRecord',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="trainingRecordForm" controller="trainingRecord" action="update">
                <g:render template="/trainingRecord/form" model="[trainingRecord:trainingRecord]"/>
                <el:hiddenField name="id" value="${trainingRecord?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>