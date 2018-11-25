<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'ExternalTransferListEmployee List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'externalTransferListEmployee',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${externalTransferListEmployee?.currentEmploymentRecord}" type="EmploymentRecord" label="${message(code:'externalTransferListEmployee.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" />
    <lay:showElement value="${externalTransferListEmployee?.effectiveDate}" type="ZonedDateTime" label="${message(code:'externalTransferListEmployee.effectiveDate.label',default:'effectiveDate')}" />
    <lay:showElement value="${externalTransferListEmployee?.employee}" type="Employee" label="${message(code:'externalTransferListEmployee.employee.label',default:'employee')}" />
    <lay:showElement value="${externalTransferListEmployee?.externalTransferList}" type="ExternalTransferList" label="${message(code:'externalTransferListEmployee.externalTransferList.label',default:'externalTransferList')}" />
    <lay:showElement value="${externalTransferListEmployee?.externalTransferListEmployeeNotes}" type="Set" label="${message(code:'externalTransferListEmployee.externalTransferListEmployeeNotes.label',default:'externalTransferListEmployeeNotes')}" />
    <lay:showElement value="${externalTransferListEmployee?.externalTransferRequest}" type="ExternalTransferRequest" label="${message(code:'externalTransferListEmployee.externalTransferRequest.label',default:'externalTransferRequest')}" />
    <lay:showElement value="${externalTransferListEmployee?.recordStatus}" type="enum" label="${message(code:'externalTransferListEmployee.recordStatus.label',default:'recordStatus')}" messagePrefix="EnumListRecordStatus" />
    <lay:showElement value="${externalTransferListEmployee?.toOrganizationId}" type="Long" label="${message(code:'externalTransferListEmployee.toOrganizationId.label',default:'toOrganizationId')}" />
</lay:showWidget>
<el:row />

</body>
</html>