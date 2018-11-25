<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'TrainingListEmployeeNote List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'trainingListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="trainingListEmployeeNoteForm" controller="trainingListEmployeeNote" action="update">
                <g:render template="/trainingListEmployeeNote/form" model="[trainingListEmployeeNote:trainingListEmployeeNote]"/>
                <el:hiddenField name="id" value="${trainingListEmployeeNote?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>