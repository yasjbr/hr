<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'allowanceListEmployeeNote.entity', default: 'AllowanceListEmployeeNote List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'AllowanceListEmployeeNote List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'allowanceListEmployeeNote', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${allowanceListEmployeeNote?.orderNo}" type="String"
                     label="${message(code: 'allowanceListEmployeeNote.orderNo.label', default: 'orderNo')}"/>
    <lay:showElement
            value="${allowanceListEmployeeNote?.allowanceListEmployee?.allowanceRequest?.employee?.transientData?.personDTO?.localFullName}"
            type="string"
            label="${message(code: 'allowanceListEmployeeNote.allowanceListEmployee.label', default: 'allowanceListEmployee')}"/>
    <lay:showElement value="${allowanceListEmployeeNote?.note}" type="String"
                     label="${message(code: 'allowanceListEmployeeNote.note.label', default: 'note')}"/>
    <lay:showElement value="${allowanceListEmployeeNote?.noteDate}" type="ZonedDate"
                     label="${message(code: 'allowanceListEmployeeNote.noteDate.label', default: 'noteDate')}"/>
</lay:showWidget>
<el:row/>

</body>
</html>