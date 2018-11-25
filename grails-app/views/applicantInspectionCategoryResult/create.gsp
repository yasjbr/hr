<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ApplicantInspectionCategoryResult List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'applicantInspectionCategoryResult',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="applicantInspectionCategoryResultForm" controller="applicantInspectionCategoryResult" action="save">
                <g:render template="/applicantInspectionCategoryResult/form" model="[applicantInspectionCategoryResult:applicantInspectionCategoryResult]"/>
                <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
                <el:formButton isSubmit="true" functionName="saveAndCreate"/>
                <el:formButton functionName="back"  onClick="window.location.href='${createLink(controller:'applicantInspectionCategoryResult',action:'list')}'"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
