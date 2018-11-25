<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'vacationListEmployee.entity', default: 'VacationListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'VacationListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'vacationListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${vacationListEmployee?.currentEmployeeMilitaryRank}" type="EmployeePromotion" label="${message(code:'vacationListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" />
    <lay:showElement value="${vacationListEmployee?.currentEmploymentRecord}" type="EmploymentRecord" label="${message(code:'vacationListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" />
    <lay:showElement value="${vacationListEmployee?.recordStatus}" type="enum" label="${message(code:'vacationListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${vacationListEmployee?.vacationList}" type="VacationList" label="${message(code:'vacationListEmployee.vacationList.label',default:'vacationList')}" />
    <lay:showElement value="${vacationListEmployee?.vacationListEmployeeNotes}" type="Set" label="${message(code:'vacationListEmployee.vacationListEmployeeNotes.label',default:'vacationListEmployeeNotes')}" />
    <lay:showElement value="${vacationListEmployee?.vacationRequest}" type="VacationRequest" label="${message(code:'vacationListEmployee.vacationRequest.label',default:'vacationRequest')}" />
</lay:showWidget>
<el:row />

</body>
</html>