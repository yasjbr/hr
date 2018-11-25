<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${joinedEmployeeOperationalTasks?.fromDate}" type="ZonedDateTime" label="${message(code:'joinedEmployeeOperationalTasks.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${joinedEmployeeOperationalTasks?.operationalTask}" type="OperationalTask" label="${message(code:'joinedEmployeeOperationalTasks.operationalTask.label',default:'operationalTask')}" />
    <lay:showElement value="${joinedEmployeeOperationalTasks?.toDate}" type="ZonedDateTime" label="${message(code:'joinedEmployeeOperationalTasks.toDate.label',default:'toDate')}" />
</lay:showWidget>