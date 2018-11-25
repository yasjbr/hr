<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'recruitmentListEmployee.entity', default: 'RecruitmentListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'RecruitmentListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${recruitmentListEmployee?.applicant}" type="Applicant" label="${message(code:'recruitmentListEmployee.applicant.label',default:'applicant')}" />
    <lay:showElement value="${recruitmentListEmployee?.note}" type="String" label="${message(code:'recruitmentListEmployee.note.label',default:'note')}" />
    <lay:showElement value="${recruitmentListEmployee?.orderNo}" type="String" label="${message(code:'recruitmentListEmployee.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${recruitmentListEmployee?.recordStatus}" type="enum" label="${message(code:'recruitmentListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${recruitmentListEmployee?.recruitmentList}" type="RecruitmentList" label="${message(code:'recruitmentListEmployee.recruitmentList.label',default:'recruitmentList')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentListEmployee',action:'list')}'"/>
</div>
</body>
</html>