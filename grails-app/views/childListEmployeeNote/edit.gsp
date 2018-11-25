<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ChildListEmployeeNote List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'childListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="childListEmployeeNoteForm" controller="childListEmployeeNote" action="update">
                <g:render template="/childListEmployeeNote/form" model="[childListEmployeeNote:childListEmployeeNote]"/>
                <el:hiddenField name="id" value="${childListEmployeeNote?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>