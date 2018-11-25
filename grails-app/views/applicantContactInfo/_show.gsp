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
    <lay:showElement value="${applicantContactInfo?.contactMethod?.descriptionInfo?.localName}" type="String" label="${message(code:'contactInfo.contactMethod.label',default:'contactMethod')}" />
    <lay:showElement value="${applicantContactInfo?.contactType?.descriptionInfo?.localName}" type="String" label="${message(code:'contactInfo.contactType.label',default:'contactType')}" />

    <g:if test="${applicantContactInfo?.address}">
        <lay:showElement value="${address}" type="String" label="${message(code:'contactInfo.value.label',default:'value')}" />
    </g:if>
    <g:else>
        <lay:showElement value="${applicantContactInfo?.value}" type="String" label="${message(code:'contactInfo.value.label',default:'value')}" />
    </g:else>
</lay:showWidget>

<el:row />
</body>
</html>