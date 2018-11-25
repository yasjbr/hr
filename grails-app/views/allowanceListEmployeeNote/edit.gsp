<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'allowanceListEmployeeNote.entity', default: 'AllowanceListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'AllowanceListEmployeeNote List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'allowanceListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="allowanceListEmployeeNoteForm" controller="allowanceListEmployeeNote" action="update">
                <g:render template="/allowanceListEmployeeNote/form" model="[allowanceListEmployeeNote:allowanceListEmployeeNote]"/>
                <el:hiddenField name="id" value="${allowanceListEmployeeNote?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>