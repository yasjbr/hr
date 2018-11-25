<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'DisciplinaryListJudgmentSetup List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'disciplinaryListJudgmentSetup',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="disciplinaryListJudgmentSetupForm" controller="disciplinaryListJudgmentSetup" action="update">
                <g:render template="/disciplinaryListJudgmentSetup/form" model="[disciplinaryListJudgmentSetup:disciplinaryListJudgmentSetup]"/>
                <el:hiddenField name="id" value="${disciplinaryListJudgmentSetup?.id}" />
                <el:formButton isSubmit="true" withPreviousLink="true"  functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>