<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceListEmployee.entity', default: 'ReturnFromAbsenceListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ReturnFromAbsenceListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${returnFromAbsenceListEmployee?.actualAbsenceReason}" type="enum" label="${message(code:'returnFromAbsenceListEmployee.actualAbsenceReason.label',default:'actualAbsenceReason')}" messagePrefix="EnumAbsenceReason" />
    <lay:showElement value="${returnFromAbsenceListEmployee?.actualReturnDate}" type="ZonedDateTime" label="${message(code:'returnFromAbsenceListEmployee.actualReturnDate.label',default:'actualReturnDate')}" />
    <lay:showElement value="${returnFromAbsenceListEmployee?.recordStatus}" type="enum" label="${message(code:'returnFromAbsenceListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${returnFromAbsenceListEmployee?.returnFromAbsenceList}" type="ReturnFromAbsenceList" label="${message(code:'returnFromAbsenceListEmployee.returnFromAbsenceList.label',default:'returnFromAbsenceList')}" />
    <lay:showElement value="${returnFromAbsenceListEmployee?.returnFromAbsenceListEmployeeNotes}" type="Set" label="${message(code:'returnFromAbsenceListEmployee.returnFromAbsenceListEmployeeNotes.label',default:'returnFromAbsenceListEmployeeNotes')}" />
    <lay:showElement value="${returnFromAbsenceListEmployee?.returnFromAbsenceRequest}" type="ReturnFromAbsenceRequest" label="${message(code:'returnFromAbsenceListEmployee.returnFromAbsenceRequest.label',default:'returnFromAbsenceRequest')}" />
</lay:showWidget>
<el:row />

</body>
</html>