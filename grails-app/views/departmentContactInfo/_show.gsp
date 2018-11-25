<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'DepartmentContactInfo List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${departmentContactInfo?.transientData?.contactTypeName}" type="String"
                     label="${message(code: 'departmentContactInfo.contactTypeId.label', default: 'contactType')}"/>
    <lay:showElement value="${departmentContactInfo?.transientData?.contactMethodName}" type="String"
                     label="${message(code: 'departmentContactInfo.contactMethodId.label', default: 'contactMethod')}"/>
    <lay:showElement value="${departmentContactInfo?.value}" type="String"
                     label="${message(code: 'departmentContactInfo.value.label', default: 'value')}"/>
</lay:showWidget>

<el:row/>
</body>
</html>