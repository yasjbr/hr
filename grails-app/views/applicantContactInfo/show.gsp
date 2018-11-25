<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'contactInfo.entity', default: 'applicantContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'applicantContactInfo List')}" />
    <title>${title}</title>
</head>
<body>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${applicantContactInfo?.contactTypeName}" type="String" label="${message(code:'contactInfo.contactType.label',default:'contactTypeId')}" />
    <lay:showElement value="${applicantContactInfo?.contactMethodName}" type="String" label="${message(code:'contactInfo.contactMethod.label',default:'contactMethodId')}" />
    <lay:showElement value="${applicantContactInfo?.fromDate}" type="ZonedDate" label="${message(code:'contactInfo.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${applicantContactInfo?.toDate}" type="ZonedDate" label="${message(code:'contactInfo.toDate.label',default:'toDate')}" />
    <lay:showElement value="${applicantContactInfo?.value}" type="String" label="${message(code:'contactInfo.value.label',default:'value')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'tabs',action:'list')}'"/>
</div>
</body>
</html>