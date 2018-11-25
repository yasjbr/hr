<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceListEmployeeNote.entity', default: 'ReturnFromAbsenceListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ReturnFromAbsenceListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="returnFromAbsenceListEmployeeNoteForm" callLoadingFunction="performPostActionWithEncodedId" controller="returnFromAbsenceListEmployeeNote" action="save">
                <g:render template="/returnFromAbsenceListEmployeeNote/form" model="[returnFromAbsenceListEmployeeNote:returnFromAbsenceListEmployeeNote]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
