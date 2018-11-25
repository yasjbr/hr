<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'employeeVacationBalance.entity', default: 'EmployeeVacationBalance List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'EmployeeVacationBalance List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'employeeVacationBalance', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeeVacationBalance?.employee?.transientData?.personDTO?.localFullName}"
                     type="Employee"
                     label="${message(code: 'employeeVacationBalance.employee.label', default: 'employee')}"/>
    <lay:showElement value="${employeeVacationBalance?.vacationConfiguration?.vacationType?.descriptionInfo?.localName}"
                     type="VacationConfiguration"
                     label="${message(code: 'employeeVacationBalance.vacationConfiguration.label', default: 'vacationConfiguration')}"/>
    <lay:showElement value="${employeeVacationBalance?.annualBalance ?: '0'}" type="Short"
                     label="${message(code: 'employeeVacationBalance.annualBalance.label', default: 'annualBalance')}"/>
    <lay:showElement value="${employeeVacationBalance?.balance ?: '0'}" type="Short"
                     label="${message(code: 'employeeVacationBalance.balance.label', default: 'balance')}"/>
    <lay:showElement value="${employeeVacationBalance?.numberOfTimesUsed ?: '0'}" type="Long"
                     label="${message(code: 'employeeVacationBalance.numberOfTimesUsed.label', default: 'numberOfTimesUsed')}"/>
    <lay:showElement value="${employeeVacationBalance?.oldTransferBalance ?: '0'}" type="Short"
                     label="${message(code: 'employeeVacationBalance.oldTransferBalance.label', default: 'oldTransferBalance')}"/>
    <lay:showElement value="${employeeVacationBalance?.validFromDate}" type="ZonedDate"
                     label="${message(code: 'employeeVacationBalance.validFromDate.label', default: 'validFromDate')}"/>
    <lay:showElement value="${employeeVacationBalance?.validToDate}" type="ZonedDate"
                     label="${message(code: 'employeeVacationBalance.validToDate.label', default: 'validToDate')}"/>
    <lay:showElement value="${employeeVacationBalance?.vacationDueYear ?: '0'}" type="Short"
                     label="${message(code: 'employeeVacationBalance.vacationDueYear.label', default: 'vacationDueYear')}"/>
</lay:showWidget>
<el:row/>
<div class="clearfix form-actions text-center">
    <btn:backButton goToPreviousLink="true"/>
</div>

</body>
</html>