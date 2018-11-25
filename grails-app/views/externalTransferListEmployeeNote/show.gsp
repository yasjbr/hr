<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalTransferListEmployeeNote.entity', default: 'ExternalTransferListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ExternalTransferListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'externalTransferListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${externalTransferListEmployeeNote?.externalTransferListEmployee}" type="ExternalTransferListEmployee" label="${message(code:'externalTransferListEmployeeNote.externalTransferListEmployee.label',default:'externalTransferListEmployee')}" />
    <lay:showElement value="${externalTransferListEmployeeNote?.note}" type="String" label="${message(code:'externalTransferListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${externalTransferListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'externalTransferListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${externalTransferListEmployeeNote?.orderNo}" type="String" label="${message(code:'externalTransferListEmployeeNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>
<el:row />

</body>
</html>