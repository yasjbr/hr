<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'departmentType.entity', default: 'DepartmentType List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'DepartmentType List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'departmentType',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${departmentType?.descriptionInfo?.localName}" type="DescriptionInfo" label="${message(code:'departmentType.descriptionInfo.localName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${departmentType?.descriptionInfo?.latinName}" type="DescriptionInfo" label="${message(code:'departmentType.descriptionInfo.latinName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${departmentType?.descriptionInfo?.hebrewName }" type="DescriptionInfo" label="${message(code:'departmentType.descriptionInfo.hebrewName.label',default:'descriptionInfo')}" />
    <lay:showElement value="${departmentType?.staticDepartmentType}" type="enum" label="${message(code:'departmentType.staticDepartmentType.label',default:'staticDepartmentType')}" messagePrefix="EnumDepartmentType" />
    <lay:showElement value="${departmentType?.universalCode}" type="String" label="${message(code:'departmentType.universalCode.label',default:'universalCode')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:editButton onClick="window.location.href='${createLink(controller:'departmentType',action:'edit',params:[encodedId: departmentType?.encodedId])}'"/>
</div>
</body>
</html>