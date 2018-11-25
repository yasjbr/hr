<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionResultListEmployee.entity', default: 'ApplicantInspectionResultListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ApplicantInspectionResultListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'applicantInspectionResultListEmployee',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="applicantInspectionResultListEmployeeForm" callLoadingFunction="performPostActionWithEncodedId" controller="applicantInspectionResultListEmployee" action="save">
                <g:render template="/applicantInspectionResultListEmployee/form" model="[applicantInspectionResultListEmployee:applicantInspectionResultListEmployee]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
