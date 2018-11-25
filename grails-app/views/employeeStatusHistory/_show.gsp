<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeeStatusHistory?.employee?.transientData?.personDTO?.localFullName}" type="Employee" label="${message(code:'employeeStatusHistory.employee.label',default:'employee')}" />
    <lay:showElement value="${employeeStatusHistory?.employeeStatus}" type="EmployeeStatus" label="${message(code:'employeeStatusHistory.employeeStatus.label',default:'employeeStatus')}" />
    <lay:showElement value="${employeeStatusHistory?.fromDate}" type="ZonedDate" label="${message(code:'employeeStatusHistory.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${employeeStatusHistory?.toDate}" type="ZonedDate" label="${message(code:'employeeStatusHistory.toDate.label',default:'toDate')}" />
</lay:showWidget>