<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'RecruitmentListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${recruitmentListEmployeeNote?.note}" type="String" label="${message(code:'recruitmentListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${recruitmentListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'recruitmentListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${recruitmentListEmployeeNote?.orderNo}" type="String" label="${message(code:'recruitmentListEmployeeNote.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${recruitmentListEmployeeNote?.recruitmentListEmployee}" type="RecruitmentListEmployee" label="${message(code:'recruitmentListEmployeeNote.recruitmentListEmployee.label',default:'recruitmentListEmployee')}" />
</lay:showWidget>
<el:row />

</body>
</html>