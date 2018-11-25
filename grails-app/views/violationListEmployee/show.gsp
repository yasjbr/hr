<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'violationListEmployee.entity', default: 'ViolationListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ViolationListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'violationListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${violationListEmployee?.absence}" type="Absence" label="${message(code:'violationListEmployee.absence.label',default:'absence')}" />
    <lay:showElement value="${violationListEmployee?.violationList}" type="ViolationList" label="${message(code:'violationListEmployee.violationList.label',default:'violationList')}" />
    <lay:showElement value="${violationListEmployee?.violationListEmployeeNotes}" type="Set" label="${message(code:'violationListEmployee.violationListEmployeeNotes.label',default:'violationListEmployeeNotes')}" />
</lay:showWidget>
<el:row />

</body>
</html>