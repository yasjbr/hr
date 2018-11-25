<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'serviceListEmployee.entity', default: 'ServiceListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ServiceListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'serviceListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${serviceListEmployee?.currentEmployeeMilitaryRank}" type="EmployeePromotion" label="${message(code:'serviceListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" />
    <lay:showElement value="${serviceListEmployee?.currentEmploymentRecord}" type="EmploymentRecord" label="${message(code:'serviceListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" />
    <lay:showElement value="${serviceListEmployee?.dateEffective}" type="ZonedDateTime" label="${message(code:'serviceListEmployee.dateEffective.label',default:'dateEffective')}" />
    <lay:showElement value="${serviceListEmployee?.employee}" type="Employee" label="${message(code:'serviceListEmployee.employee.label',default:'employee')}" />
    <lay:showElement value="${serviceListEmployee?.employmentServiceRequest}" type="EmploymentServiceRequest" label="${message(code:'serviceListEmployee.employmentServiceRequest.label',default:'employmentServiceRequest')}" />
    <lay:showElement value="${serviceListEmployee?.recordStatus}" type="enum" label="${message(code:'serviceListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${serviceListEmployee?.serviceActionReason}" type="ServiceActionReason" label="${message(code:'serviceListEmployee.serviceActionReason.label',default:'serviceActionReason')}" />
    <lay:showElement value="${serviceListEmployee?.serviceList}" type="ServiceList" label="${message(code:'serviceListEmployee.serviceList.label',default:'serviceList')}" />
    <lay:showElement value="${serviceListEmployee?.serviceListEmployeeNotes}" type="Set" label="${message(code:'serviceListEmployee.serviceListEmployeeNotes.label',default:'serviceListEmployeeNotes')}" />
</lay:showWidget>
<el:row />

</body>
</html>