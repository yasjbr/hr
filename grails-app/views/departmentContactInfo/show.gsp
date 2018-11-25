<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DepartmentContactInfo List')}" />
    <title>${title}</title>
</head>
<body>

<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${departmentContactInfo?.contactTypeName}" type="String" label="${message(code:'departmentContactInfo.contactTypeId.label',default:'contactTypeId')}" />
    <lay:showElement value="${departmentContactInfo?.contactMethodName}" type="String" label="${message(code:'departmentContactInfo.contactMethodId.label',default:'contactMethodId')}" />
    <lay:showElement value="${departmentContactInfo?.fromDate}" type="ZonedDate" label="${message(code:'departmentContactInfo.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${departmentContactInfo?.toDate}" type="ZonedDate" label="${message(code:'departmentContactInfo.toDate.label',default:'toDate')}" />
    <lay:showElement value="${departmentContactInfo?.value}" type="String" label="${message(code:'departmentContactInfo.value.label',default:'value')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'tabs',action:'list')}'"/>
</div>
</body>
</html>