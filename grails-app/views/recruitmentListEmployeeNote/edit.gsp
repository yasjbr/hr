<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'RecruitmentListEmployeeNote List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="recruitmentListEmployeeNoteForm" controller="recruitmentListEmployeeNote" action="update">
                <g:render template="/recruitmentListEmployeeNote/form" model="[recruitmentListEmployeeNote:recruitmentListEmployeeNote]"/>
                <el:hiddenField name="id" value="${recruitmentListEmployeeNote?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>