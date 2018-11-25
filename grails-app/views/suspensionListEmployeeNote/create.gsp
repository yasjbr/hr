<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionListEmployeeNote.entity', default: 'SuspensionListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'SuspensionListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="suspensionListEmployeeNoteForm" callLoadingFunction="performPostActionWithEncodedId" controller="suspensionListEmployeeNote" action="save">
                <g:render template="/suspensionListEmployeeNote/form" model="[suspensionListEmployeeNote:suspensionListEmployeeNote]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
