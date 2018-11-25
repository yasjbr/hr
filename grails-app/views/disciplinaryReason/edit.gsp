<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason List')}"/>
    <g:set var="title"
           value="${message(code: 'default.edit.label', args: [entity], default: 'DisciplinaryReason List')}"/>
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
            <el:validatableForm name="disciplinaryReasonForm" controller="disciplinaryReason" action="update">
                <g:render template="/disciplinaryReason/form" model="[disciplinaryReason: disciplinaryReason]"/>
                <el:hiddenField name="id" value="${disciplinaryReason?.id}"/>
                <el:formButton isSubmit="true" withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/></el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>