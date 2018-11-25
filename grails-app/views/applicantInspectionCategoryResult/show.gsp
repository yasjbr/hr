<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ApplicantInspectionCategoryResult List')}" />
    <title>${title}</title>
</head>
<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${applicantInspectionCategoryResult?.applicant}" type="Applicant" label="${message(code:'applicantInspectionCategoryResult.applicant.label',default:'applicant')}" />
    <lay:showElement value="${applicantInspectionCategoryResult?.inspectionCategory}" type="InspectionCategory" label="${message(code:'applicantInspectionCategoryResult.inspectionCategory.label',default:'inspectionCategory')}" />
    <lay:showElement value="${applicantInspectionCategoryResult?.inspectionResult}" type="enum" label="${message(code:'applicantInspectionCategoryResult.inspectionResult.label',default:'inspectionResult')}" messagePrefix="EnumInspectionResult" />
    <lay:showElement value="${applicantInspectionCategoryResult?.mark}" type="String" label="${message(code:'applicantInspectionCategoryResult.mark.label',default:'mark')}" />
    <lay:showElement value="${applicantInspectionCategoryResult?.requestDate}" type="ZonedDateTime" label="${message(code:'applicantInspectionCategoryResult.requestDate.label',default:'requestDate')}" />
    <lay:showElement value="${applicantInspectionCategoryResult?.resultSummary}" type="String" label="${message(code:'applicantInspectionCategoryResult.resultSummary.label',default:'resultSummary')}" />
    <lay:showElement value="${applicantInspectionCategoryResult?.testsResult}" type="Set" label="${message(code:'applicantInspectionCategoryResult.testsResult.label',default:'testsResult')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'applicantInspectionCategoryResult',action:'list')}'"/>
    <btn:editButton onClick="window.location.href='${createLink(controller:'applicantInspectionCategoryResult',action:'edit',params: [encodedId:applicantInspectionCategoryResult?.encodedId])}'"/>
</div>
</body>
</html>