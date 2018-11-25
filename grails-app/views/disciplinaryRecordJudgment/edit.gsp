<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryRecordJudgment.entity', default: 'DisciplinaryRecordJudgment List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'DisciplinaryRecordJudgment List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'disciplinaryRecordJudgment',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="disciplinaryRecordJudgmentForm" controller="disciplinaryRecordJudgment" action="update">
                <g:render template="/disciplinaryRecordJudgment/form" model="[disciplinaryRecordJudgment:disciplinaryRecordJudgment]"/>
                <el:hiddenField name="id" value="${disciplinaryRecordJudgment?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>