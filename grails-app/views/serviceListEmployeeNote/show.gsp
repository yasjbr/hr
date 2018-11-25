<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'serviceListEmployeeNote.entity', default: 'ServiceListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ServiceListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'serviceListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${serviceListEmployeeNote?.note}" type="String" label="${message(code:'serviceListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${serviceListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'serviceListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${serviceListEmployeeNote?.orderNo}" type="String" label="${message(code:'serviceListEmployeeNote.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${serviceListEmployeeNote?.serviceListEmployee}" type="ServiceListEmployee" label="${message(code:'serviceListEmployeeNote.serviceListEmployee.label',default:'serviceListEmployee')}" />
</lay:showWidget>
<el:row />

</body>
</html>