<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'DisciplinaryReason List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'disciplinaryReason', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="disciplinaryReasonForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="disciplinaryReason" action="save">
                <g:render template="/disciplinaryReason/form" model="[disciplinaryReason: disciplinaryReason]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'disciplinaryReason', action: 'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
