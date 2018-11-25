<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'firmSupportContactInfo.entity', default: 'FirmSupportContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'FirmSupportContactInfo List')}" />
    <title>${title}</title>
</head>
<body>


<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${firmSupportContactInfo?.name}" type="String" label="${message(code:'firmSupportContactInfo.name.label',default:'name')}" />
    <lay:showElement value="${firmSupportContactInfo?.phoneNumber}" type="String" label="${message(code:'firmSupportContactInfo.phoneNumber.label',default:'phoneNumber')}" />
    <lay:showElement value="${firmSupportContactInfo?.faxNumber}" type="String" label="${message(code:'firmSupportContactInfo.faxNumber.label',default:'faxNumber')}" />
    <lay:showElement value="${firmSupportContactInfo?.email}" type="String" label="${message(code:'firmSupportContactInfo.email.label',default:'email')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'firmSupportContactInfo',action:'edit',id:firmSupportContactInfo?.encodedId)}'"/>
</div>

<el:row />

</body>
</html>