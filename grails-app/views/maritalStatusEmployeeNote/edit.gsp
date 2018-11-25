<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'MaritalStatusEmployeeNote List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'maritalStatusEmployeeNote',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="maritalStatusEmployeeNoteForm" controller="maritalStatusEmployeeNote" action="update">
                <g:render template="/maritalStatusEmployeeNote/form" model="[maritalStatusEmployeeNote:maritalStatusEmployeeNote]"/>
                <el:hiddenField name="id" value="${maritalStatusEmployeeNote?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>