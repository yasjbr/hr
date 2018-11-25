<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'interview.entity', default: 'Interview List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'Interview List')}" />
    <title>${title}</title>
    <g:render template="script"/>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'interview',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm callLoadingFunction="performPostActionWithEncodedId" name="interviewForm" controller="interview" action="save">
                <g:render template="/interview/form" model="[interview:interview]"/>
                <el:formButton functionName="saveAndContinueButton" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel"  onClick="window.location.href='${createLink(controller:'interview',action:'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
