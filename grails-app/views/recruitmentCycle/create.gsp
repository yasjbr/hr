<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'RecruitmentCycle List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'recruitmentCycle', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm callLoadingFunction="performPostActionWithEncodedId" name="recruitmentCycleForm"
                                     controller="recruitmentCycle" action="save">
                <g:render template="/recruitmentCycle/form" model="[recruitmentCycle: recruitmentCycle]"/>
                <el:formButton functionName="saveAndContinueButton" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel"
                               withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>