<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'contactInfo.entity', default: 'applicantContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'applicantContactInfo List')}" />
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
            <el:validatableResetForm name="applicantContactInfoForm" controller="applicant" action="saveContactInfo">
                <g:render template="/applicantContactInfo/form" model="[applicantContactInfo:applicantContactInfo]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
