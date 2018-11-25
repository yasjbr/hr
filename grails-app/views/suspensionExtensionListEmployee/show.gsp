<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionListEmployee.entity', default: 'SuspensionExtensionListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'SuspensionExtensionListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'suspensionExtensionListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${suspensionExtensionListEmployee?.currentEmployeeMilitaryRank}" type="EmployeePromotion" label="${message(code:'suspensionExtensionListEmployee.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" />
    <lay:showElement value="${suspensionExtensionListEmployee?.currentEmploymentRecord}" type="EmploymentRecord" label="${message(code:'suspensionExtensionListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" />
    <lay:showElement value="${suspensionExtensionListEmployee?.effectiveDate}" type="ZonedDateTime" label="${message(code:'suspensionExtensionListEmployee.effectiveDate.label',default:'effectiveDate')}" />
    <lay:showElement value="${suspensionExtensionListEmployee?.fromDate}" type="ZonedDateTime" label="${message(code:'suspensionExtensionListEmployee.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${suspensionExtensionListEmployee?.periodInMonth}" type="Short" label="${message(code:'suspensionExtensionListEmployee.periodInMonth.label',default:'periodInMonth')}" />
    <lay:showElement value="${suspensionExtensionListEmployee?.recordStatus}" type="enum" label="${message(code:'suspensionExtensionListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${suspensionExtensionListEmployee?.suspensionExtensionList}" type="SuspensionExtensionList" label="${message(code:'suspensionExtensionListEmployee.suspensionExtensionList.label',default:'suspensionExtensionList')}" />
    <lay:showElement value="${suspensionExtensionListEmployee?.suspensionExtensionListEmployeeNotes}" type="Set" label="${message(code:'suspensionExtensionListEmployee.suspensionExtensionListEmployeeNotes.label',default:'suspensionExtensionListEmployeeNotes')}" />
    <lay:showElement value="${suspensionExtensionListEmployee?.suspensionExtensionRequest}" type="SuspensionExtensionRequest" label="${message(code:'suspensionExtensionListEmployee.suspensionExtensionRequest.label',default:'suspensionExtensionRequest')}" />
    <lay:showElement value="${suspensionExtensionListEmployee?.toDate}" type="ZonedDateTime" label="${message(code:'suspensionExtensionListEmployee.toDate.label',default:'toDate')}" />
</lay:showWidget>
<el:row />

</body>
</html>