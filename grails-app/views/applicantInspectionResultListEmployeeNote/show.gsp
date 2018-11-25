<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'applicantInspectionResultListEmployeeNote.entity', default: 'ApplicantInspectionResultListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ApplicantInspectionResultListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'applicantInspectionResultListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${applicantInspectionResultListEmployeeNote?.applicantInspectionResultListEmployee}" type="ApplicantInspectionResultListEmployee" label="${message(code:'applicantInspectionResultListEmployeeNote.applicantInspectionResultListEmployee.label',default:'applicantInspectionResultListEmployee')}" />
    <lay:showElement value="${applicantInspectionResultListEmployeeNote?.note}" type="String" label="${message(code:'applicantInspectionResultListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${applicantInspectionResultListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'applicantInspectionResultListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${applicantInspectionResultListEmployeeNote?.orderNo}" type="String" label="${message(code:'applicantInspectionResultListEmployeeNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>
<el:row />

</body>
</html>