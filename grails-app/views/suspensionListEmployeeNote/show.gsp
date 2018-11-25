<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionListEmployeeNote.entity', default: 'SuspensionListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'SuspensionListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${suspensionListEmployeeNote?.note}" type="String" label="${message(code:'suspensionListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${suspensionListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'suspensionListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${suspensionListEmployeeNote?.orderNo}" type="String" label="${message(code:'suspensionListEmployeeNote.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${suspensionListEmployeeNote?.suspensionListEmployee}" type="SuspensionListEmployee" label="${message(code:'suspensionListEmployeeNote.suspensionListEmployee.label',default:'suspensionListEmployee')}" />
</lay:showWidget>
<el:row />

</body>
</html>