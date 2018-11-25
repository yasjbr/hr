<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'generalListEmployee.entity', default: 'GeneralListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'GeneralListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'generalListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${generalListEmployee?.employee}" type="Employee" label="${message(code:'generalListEmployee.employee.label',default:'employee')}" />
    <lay:showElement value="${generalListEmployee?.employeeMilitaryRank}" type="EmployeePromotion" label="${message(code:'generalListEmployee.employeeMilitaryRank.label',default:'employeeMilitaryRank')}" />
    <lay:showElement value="${generalListEmployee?.generalList}" type="GeneralList" label="${message(code:'generalListEmployee.generalList.label',default:'generalList')}" />
    <lay:showElement value="${generalListEmployee?.generalListEmployeeNotes}" type="Set" label="${message(code:'generalListEmployee.generalListEmployeeNotes.label',default:'generalListEmployeeNotes')}" />
    <lay:showElement value="${generalListEmployee?.recordStatus}" type="enum" label="${message(code:'generalListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
</lay:showWidget>
<el:row />

</body>
</html>