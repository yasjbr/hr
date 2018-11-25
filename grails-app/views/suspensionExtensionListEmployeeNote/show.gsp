<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionListEmployeeNote.entity', default: 'SuspensionExtensionListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'SuspensionExtensionListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionExtensionListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${suspensionExtensionListEmployeeNote?.note}" type="String" label="${message(code:'suspensionExtensionListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${suspensionExtensionListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'suspensionExtensionListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${suspensionExtensionListEmployeeNote?.orderNo}" type="String" label="${message(code:'suspensionExtensionListEmployeeNote.orderNo.label',default:'orderNo')}" />
    <lay:showElement value="${suspensionExtensionListEmployeeNote?.suspensionExtinsionListEmployee}" type="SuspensionExtensionListEmployee" label="${message(code:'suspensionExtensionListEmployeeNote.suspensionExtinsionListEmployee.label',default:'suspensionExtinsionListEmployee')}" />
</lay:showWidget>
<el:row />

</body>
</html>