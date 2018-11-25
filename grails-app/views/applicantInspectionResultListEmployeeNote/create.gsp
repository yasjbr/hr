<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionResultListEmployeeNote.entity', default: 'ApplicantInspectionResultListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ApplicantInspectionResultListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'applicantInspectionResultListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="applicantInspectionResultListEmployeeNoteForm" callLoadingFunction="performPostActionWithEncodedId" controller="applicantInspectionResultListEmployeeNote" action="save">
                <g:render template="/applicantInspectionResultListEmployeeNote/form" model="[applicantInspectionResultListEmployeeNote:applicantInspectionResultListEmployeeNote]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
