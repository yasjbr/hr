<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'contactInfo.entity', default: 'contactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'contactInfo List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'applicant',action:'filterContactInfo')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="applicantContactInfoForm" controller="applicant" action="updateContactInfo">
                <g:render template="/applicantContactInfo/form" model="[applicantContactInfo:applicantContactInfo]"/>
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>