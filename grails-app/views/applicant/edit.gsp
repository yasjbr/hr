<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'Applicant List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'Applicant List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'applicant',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="applicantForm" controller="applicant" action="update">
                <el:hiddenField name="id" value="${applicant?.id}" />
                <g:render template="/applicant/form" model="[applicant:applicant, applicantStatusList:applicantStatusList]"/>
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>