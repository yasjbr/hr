<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'MaritalStatusEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'maritalStatusEmployeeNote',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${maritalStatusEmployeeNote?.maritalStatusListEmployee}" type="MaritalStatusListEmployee" label="${message(code:'maritalStatusEmployeeNote.maritalStatusListEmployee.label',default:'maritalStatusListEmployee')}" />
    <lay:showElement value="${maritalStatusEmployeeNote?.note}" type="String" label="${message(code:'maritalStatusEmployeeNote.note.label',default:'note')}" />
    <lay:showElement value="${maritalStatusEmployeeNote?.noteDate}" type="ZonedDateTime" label="${message(code:'maritalStatusEmployeeNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${maritalStatusEmployeeNote?.orderNo}" type="String" label="${message(code:'maritalStatusEmployeeNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>
<el:row />

</body>
</html>