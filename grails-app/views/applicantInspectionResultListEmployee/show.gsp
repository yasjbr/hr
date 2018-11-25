<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionResultListEmployee.entity', default: 'ApplicantInspectionResultListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ApplicantInspectionResultListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'applicantInspectionResultListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${applicantInspectionResultListEmployee?.applicant}" type="Applicant" label="${message(code:'applicantInspectionResultListEmployee.applicant.label',default:'applicant')}" />
    <lay:showElement value="${applicantInspectionResultListEmployee?.applicantInspectionResultList}" type="ApplicantInspectionResultList" label="${message(code:'applicantInspectionResultListEmployee.applicantInspectionResultList.label',default:'applicantInspectionResultList')}" />
    <lay:showElement value="${applicantInspectionResultListEmployee?.applicantInspectionResultListEmployeeNotes}" type="Set" label="${message(code:'applicantInspectionResultListEmployee.applicantInspectionResultListEmployeeNotes.label',default:'applicantInspectionResultListEmployeeNotes')}" />
    <lay:showElement value="${applicantInspectionResultListEmployee?.recordStatus}" type="enum" label="${message(code:'applicantInspectionResultListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
</lay:showWidget>
<el:row />

</body>
</html>