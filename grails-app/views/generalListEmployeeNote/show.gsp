<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'generalListEmployeeNote.entity', default: 'GeneralListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'GeneralListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'generalListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${generalListEmployeeNote?.generalListEmployee}" type="GeneralListEmployee" label="${message(code:'generalListEmployeeNote.generalListEmployee.label',default:'generalListEmployee')}" />
    <lay:showElement value="${generalListEmployeeNote?.note}" type="String" label="${message(code:'generalListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${generalListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'generalListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${generalListEmployeeNote?.orderNo}" type="String" label="${message(code:'generalListEmployeeNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>
<el:row />

</body>
</html>