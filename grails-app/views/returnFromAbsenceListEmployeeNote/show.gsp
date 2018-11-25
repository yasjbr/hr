<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceListEmployeeNote.entity', default: 'ReturnFromAbsenceListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ReturnFromAbsenceListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${returnFromAbsenceListEmployeeNote?.note}" type="String" label="${message(code:'returnFromAbsenceListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${returnFromAbsenceListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'returnFromAbsenceListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${returnFromAbsenceListEmployeeNote?.orderNo}" type="String" label="${message(code:'returnFromAbsenceListEmployeeNote.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${returnFromAbsenceListEmployeeNote?.returnFromAbsenceListEmployee}" type="ReturnFromAbsenceListEmployee" label="${message(code:'returnFromAbsenceListEmployeeNote.returnFromAbsenceListEmployee.label',default:'returnFromAbsenceListEmployee')}" />
</lay:showWidget>
<el:row />

</body>
</html>