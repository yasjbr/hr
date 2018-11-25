<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'interview.entity', default: 'Interview List')}"/>
    <g:set var="title" value="${message(code: 'default.edit.label', args: [entity], default: 'Interview List')}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'interview', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="interviewForm" controller="interview" action="update">
                <g:render template="/interview/form" model="[interview: interview]"/>
                <el:hiddenField name="id" value="${interview?.id}"/>
                <el:formButton isSubmit="true"  withPreviousLink="true" functionName="save"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>