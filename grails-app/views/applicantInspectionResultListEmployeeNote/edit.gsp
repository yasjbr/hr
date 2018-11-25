<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'applicantInspectionResultListEmployeeNote.entity', default: 'ApplicantInspectionResultListEmployeeNote List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ApplicantInspectionResultListEmployeeNote List')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller:'applicantInspectionResultListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="applicantInspectionResultListEmployeeNoteForm" controller="applicantInspectionResultListEmployeeNote" action="update">
                <el:hiddenField name="id" value="${applicantInspectionResultListEmployeeNote?.id}"/>
                <g:render template="form"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>