<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantEducation.entity', default: 'applicantEducation List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'applicantEducation List')}" />
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
            <el:validatableForm name="applicantEducationForm" controller="applicant" action="updateApplicantEducation">
                <el:hiddenField name="id" value="${applicantEducation?.id}" />
                <g:render template="/applicantEducation/form" model="[applicantEducation:applicantEducation]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>