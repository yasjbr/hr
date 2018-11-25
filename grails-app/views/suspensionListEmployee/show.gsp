<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionListEmployee.entity', default: 'SuspensionListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'SuspensionListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${suspensionListEmployee?.currentEmployeeMilitaryRank}" type="EmployeePromotion" label="${message(code:'suspensionListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" />
    <lay:showElement value="${suspensionListEmployee?.currentEmploymentRecord}" type="EmploymentRecord" label="${message(code:'suspensionListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" />
    <lay:showElement value="${suspensionListEmployee?.effectiveDate}" type="ZonedDateTime" label="${message(code:'suspensionListEmployee.effectiveDate.label',default:'effectiveDate')}" />
    <lay:showElement value="${suspensionListEmployee?.employee}" type="Employee" label="${message(code:'suspensionListEmployee.employee.label',default:'employee')}" />
    <lay:showElement value="${suspensionListEmployee?.fromDate}" type="ZonedDateTime" label="${message(code:'suspensionListEmployee.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${suspensionListEmployee?.periodInMonth}" type="Short" label="${message(code:'suspensionListEmployee.periodInMonth.label',default:'periodInMonth')}" />
    <lay:showElement value="${suspensionListEmployee?.recordStatus}" type="enum" label="${message(code:'suspensionListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${suspensionListEmployee?.suspensionList}" type="SuspensionList" label="${message(code:'suspensionListEmployee.suspensionList.label',default:'suspensionList')}" />
    <lay:showElement value="${suspensionListEmployee?.suspensionListEmployeeNotes}" type="Set" label="${message(code:'suspensionListEmployee.suspensionListEmployeeNotes.label',default:'suspensionListEmployeeNotes')}" />
    <lay:showElement value="${suspensionListEmployee?.suspensionRequest}" type="SuspensionRequest" label="${message(code:'suspensionListEmployee.suspensionRequest.label',default:'suspensionRequest')}" />
    <lay:showElement value="${suspensionListEmployee?.suspensionType}" type="enum" label="${message(code:'suspensionListEmployee.suspensionType.label',default:'suspensionType')}" messagePrefix="EnumSuspensionType" />
    <lay:showElement value="${suspensionListEmployee?.toDate}" type="ZonedDateTime" label="${message(code:'suspensionListEmployee.toDate.label',default:'toDate')}" />
</lay:showWidget>
<el:row />

</body>
</html>