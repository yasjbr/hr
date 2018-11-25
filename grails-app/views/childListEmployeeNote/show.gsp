<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ChildListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'childListEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${childListEmployeeNote?.childListEmployee}" type="ChildListEmployee" label="${message(code:'childListEmployeeNote.childListEmployee.label',default:'childListEmployee')}" />
    <lay:showElement value="${childListEmployeeNote?.note}" type="String" label="${message(code:'childListEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${childListEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'childListEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${childListEmployeeNote?.orderNo}" type="String" label="${message(code:'childListEmployeeNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>
<el:row />

</body>
</html>