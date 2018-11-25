<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'recruitmentCyclePhase.entity', default: 'RecruitmentCyclePhase List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'RecruitmentCyclePhase List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentCyclePhase',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="recruitmentCyclePhaseForm" controller="recruitmentCyclePhase" action="save">
                <g:render template="/recruitmentCyclePhase/form" model="[recruitmentCyclePhase:recruitmentCyclePhase]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
