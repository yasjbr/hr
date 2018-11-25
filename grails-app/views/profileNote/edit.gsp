<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'profileNote.entity', default: 'ProfileNote List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ProfileNote List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'profileNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="profileNoteForm" controller="profileNote" action="update">
                <g:render template="/profileNote/form" model="[profileNote:profileNote]"/>
                <el:hiddenField name="id" value="${profileNote?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>