<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'disciplinaryJudgment.entity', default: 'disciplinaryJudgment List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'disciplinaryJudgment List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'disciplinaryJudgment', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="disciplinaryJudgmentForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="disciplinaryJudgment" action="save">
                <g:render template="/disciplinaryJudgment/form" model="[disciplinaryJudgment: disciplinaryJudgment]"/>

                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'disciplinaryJudgment', action: 'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
