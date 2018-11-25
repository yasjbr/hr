<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryRecordJudgment.entity', default: 'DisciplinaryRecordJudgment List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'DisciplinaryRecordJudgment List')}" />
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
            <el:validatableResetForm name="disciplinaryRecordJudgmentForm" callLoadingFunction="performPostActionWithEncodedId" controller="disciplinaryRecordJudgment" action="save">
                <g:render template="/disciplinaryRecordJudgment/form" model="[disciplinaryRecordJudgment:disciplinaryRecordJudgment]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
