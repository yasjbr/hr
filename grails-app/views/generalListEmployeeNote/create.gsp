<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'generalListEmployeeNote.entity', default: 'GeneralListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'GeneralListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'generalListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="generalListEmployeeNoteForm" callLoadingFunction="performPostActionWithEncodedId" controller="generalListEmployeeNote" action="save">
                <g:render template="/generalListEmployeeNote/form" model="[generalListEmployeeNote:generalListEmployeeNote]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
