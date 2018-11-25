<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionListEmployeeNote.entity', default: 'SuspensionExtensionListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'SuspensionExtensionListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionExtensionListEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="suspensionExtensionListEmployeeNoteForm" callLoadingFunction="performPostActionWithEncodedId" controller="suspensionExtensionListEmployeeNote" action="save">
                <g:render template="/suspensionExtensionListEmployeeNote/form" model="[suspensionExtensionListEmployeeNote:suspensionExtensionListEmployeeNote]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
