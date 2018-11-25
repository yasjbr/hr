<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'DisciplinaryListJudgmentSetup List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'disciplinaryListJudgmentSetup', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="disciplinaryListJudgmentSetupForm"
                                     callLoadingFunction="performPostActionWithEncodedId"
                                     controller="disciplinaryListJudgmentSetup" action="save">
                <g:render template="/disciplinaryListJudgmentSetup/form"
                          model="[disciplinaryListJudgmentSetup: disciplinaryListJudgmentSetup]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel"
                               onClick="window.location.href='${createLink(controller: 'disciplinaryListJudgmentSetup', action: 'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
