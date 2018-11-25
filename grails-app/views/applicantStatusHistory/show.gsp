<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantStatusHistory.entity', default: 'ApplicantStatusHistory List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ApplicantStatusHistory List')}" />
    <title>${title}</title>
</head>
<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${applicantStatusHistory?.applicant?.personId}" type="Applicant" label="${message(code:'applicantStatusHistory.applicant.label',default:'applicant')}" />
    <lay:showElement value="${applicantStatusHistory?.applicantStatus}" type="enum" label="${message(code:'applicantStatusHistory.applicantStatus.label',default:'applicantStatus')}" messagePrefix="EnumApplicantStatus" />
    <lay:showElement value="${applicantStatusHistory?.fromDate}" type="ZonedDate" label="${message(code:'applicantStatusHistory.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${applicantStatusHistory?.toDate}" type="ZonedDate" label="${message(code:'applicantStatusHistory.toDate.label',default:'toDate')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'applicantStatusHistory',action:'list')}'"/>
</div>
</body>
</html>