<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'recruitmentCyclePhase.entity', default: 'RecruitmentCyclePhase List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'RecruitmentCyclePhase List')}" />
    <title>${title}</title>
</head>
<body>
<lay:showWidget size="12" title="${title}">

    <lay:showElement value="${recruitmentCyclePhase?.recruitmentCycle.name}" type="String" label="${message(code:'recruitmentCyclePhase.recruitmentCycle.label',default:'recruitmentCycle')}" />
    <lay:showElement value="${recruitmentCyclePhase?.requisitionAnnouncementStatus}" type="enum" label="${message(code:'recruitmentCyclePhase.requisitionAnnouncementStatus.label',default:'requisitionAnnouncementStatus')}" messagePrefix="EnumRequisitionAnnouncementStatus" />
    <lay:showElement value="${recruitmentCyclePhase?.fromDate}" type="ZonedDate" label="${message(code:'recruitmentCyclePhase.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${recruitmentCyclePhase?.toDate}" type="ZonedDate" label="${message(code:'recruitmentCyclePhase.toDate.label',default:'toDate')}" />
</lay:showWidget>
<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentCyclePhase',action:'list')}'"/>
</div>
</body>
</html>