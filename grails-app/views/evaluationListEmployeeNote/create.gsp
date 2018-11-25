<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'evaluationListEmployeeNote.entity', default: 'EvaluationListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'EvaluationListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'evaluationListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="evaluationListEmployeeNoteForm" callLoadingFunction="performPostActionWithEncodedId" controller="evaluationListEmployeeNote" action="save">
                <g:render template="/evaluationListEmployeeNote/form" model="[evaluationListEmployeeNote:evaluationListEmployeeNote]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
