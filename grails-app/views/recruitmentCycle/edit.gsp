<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle List')}"/>
    <g:set var="title"
           value="${message(code: 'default.edit.label', args: [entity], default: 'RecruitmentCycle List')}"/>
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
            <el:validatableForm name="recruitmentCycleForm" controller="recruitmentCycle" action="update">
                <el:hiddenField name="encodedId" value="${recruitmentCycle?.encodedId}"/>
                <g:render template="/recruitmentCycle/form" model="[recruitmentCycle: recruitmentCycle]"/>

                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>