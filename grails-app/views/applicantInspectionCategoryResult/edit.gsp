<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'ApplicantInspectionCategoryResult List')}" />
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
            <el:validatableForm name="applicantInspectionCategoryResultForm" controller="applicantInspectionCategoryResult" action="update">
                <g:render template="/applicantInspectionCategoryResult/form" model="[applicantInspectionCategoryResult:applicantInspectionCategoryResult]"/>
                <el:hiddenField name="id" value="${applicantInspectionCategoryResult?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>