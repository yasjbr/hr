<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'JoinedRecruitmentCycleDepartment List')}" />
    <title>${title}</title>
</head>
<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${joinedRecruitmentCycleDepartment?.department}" type="Department" label="${message(code:'joinedRecruitmentCycleDepartment.department.label',default:'department')}" />
    <lay:showElement value="${joinedRecruitmentCycleDepartment?.recruitmentCycle}" type="RecruitmentCycle" label="${message(code:'joinedRecruitmentCycleDepartment.recruitmentCycle.label',default:'recruitmentCycle')}" />
    <lay:showElement value="${joinedRecruitmentCycleDepartment?.recruitmentCycleDepartmentStatus}" type="enum" label="${message(code:'joinedRecruitmentCycleDepartment.recruitmentCycleDepartmentStatus.label',default:'recruitmentCycleDepartmentStatus')}" messagePrefix="EnumRecruitmentCycleDepartmentStatus" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'joinedRecruitmentCycleDepartment',action:'list')}'"/>
</div>
</body>
</html>