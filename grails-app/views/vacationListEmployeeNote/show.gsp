<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'VacationListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'vacationListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${vacationListEmployeeNote?.note}" type="String" label="${message(code:'vacationListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${vacationListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'vacationListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${vacationListEmployeeNote?.orderNo}" type="String" label="${message(code:'vacationListEmployeeNote.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${vacationListEmployeeNote?.vacationListEmployee}" type="VacationListEmployee" label="${message(code:'vacationListEmployeeNote.vacationListEmployee.label',default:'vacationListEmployee')}" />
</lay:showWidget>
<el:row />

</body>
</html>