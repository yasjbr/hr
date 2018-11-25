<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'department.entity', default: 'Department List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'Department List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${department?.descriptionInfo?.localName}" type="DescriptionInfo"
                     label="${message(code: 'department.descriptionInfo.localName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${department?.descriptionInfo?.latinName}" type="DescriptionInfo"
                     label="${message(code: 'department.descriptionInfo.latinName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${department?.descriptionInfo?.hebrewName}" type="DescriptionInfo"
                     label="${message(code: 'department.descriptionInfo.hebrewName.label', default: 'descriptionInfo')}"/>
    <lay:showElement value="${department?.departmentType?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'department.departmentType.label', default: 'departmentType')}"
                     messagePrefix="EnumDepartmentType"/>

    <lay:showElement value="${department?.transientData?.functionalParentDeptName}" type="String"
                     label="${message(code: 'department.functionalParentDeptId.label', default: 'functionalParentDept')}"/>
    <lay:showElement value="${department?.transientData?.managerialParentDeptName}" type="String"
                     label="${message(code: 'department.managerialParentDeptId.label', default: 'managerialParentDept')}"/>

    <lay:showElement value="${department?.transientData?.directManager?.toString()}" type="String"
                     label="${message(code: 'department.directManager.label', default: 'directManager')}"/>

    <lay:showElement value="${department?.transientData?.locationName}" type="String"
                     label="${message(code: 'department.locationId.label', default: 'locationId')}"/>
    <lay:showElement value="${department?.note}" type="String"
                     label="${message(code: 'department.note.label', default: 'note')}"/>
</lay:showWidget>

<g:if test="${!isReadOnly && !params['isReadOnly']}">
    <el:row/>
    <div class="clearfix form-actions text-center">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'department', action: 'edit', params: [encodedId: department?.encodedId])}'"/>

    </div>
</g:if>
</body>
</html>