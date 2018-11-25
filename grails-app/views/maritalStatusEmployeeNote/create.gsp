<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'MaritalStatusEmployeeNote List')}" />
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
            <el:validatableResetForm name="maritalStatusEmployeeNoteForm" callLoadingFunction="performPostActionWithEncodedId" controller="maritalStatusEmployeeNote" action="save">
                <g:render template="/maritalStatusEmployeeNote/form" model="[maritalStatusEmployeeNote:maritalStatusEmployeeNote]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
