<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeeInternalAssignation?.employmentRecord?.employee}" type="Employee" label="${message(code:'employmentRecord.employee.label',default:'employee')}" />
    <lay:showElement value="${employeeInternalAssignation?.assignedToDepartment}" type="Department" label="${message(code:'employeeInternalAssignation.assignedToDepartment.label',default:'assignedToDepartment')}" />
    <lay:showElement value="${employeeInternalAssignation?.assignedToDepartmentFromDate}" type="ZonedDate" label="${message(code:'employeeInternalAssignation.assignedToDepartmentFromDate.label',default:'assignedToDepartmentFromDate')}" />
    <lay:showElement value="${employeeInternalAssignation?.assignedToDepartmentToDate}" type="ZonedDate" label="${message(code:'employeeInternalAssignation.assignedToDepartmentToDate.label',default:'assignedToDepartmentToDate')}" />
    <lay:showElement value="${employeeInternalAssignation?.employmentRecord?.department}" type="Department" label="${message(code:'employmentRecord.department.label',default:'department')}" />
    <lay:showElement value="${employeeInternalAssignation?.employmentRecord?.fromDate}" type="ZonedDate" label="${message(code:'employmentRecord.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${employeeInternalAssignation?.employmentRecord?.toDate}" type="ZonedDate" label="${message(code:'employmentRecord.toDate.label',default:'toDate')}" />
    <lay:showElement value="${employeeInternalAssignation?.employmentRecord?.employmentCategory}" type="EmploymentCategory" label="${message(code:'employmentRecord.employmentCategory.label',default:'employmentCategory')}" />
    <lay:showElement value="${employeeInternalAssignation?.employmentRecord?.jobTitle}" type="JobTitle" label="${message(code:'employmentRecord.jobTitle.label',default:'jobTitle')}" />
    <lay:showElement value="${employeeInternalAssignation?.employmentRecord?.jobDescription}" type="String" label="${message(code:'employmentRecord.jobDescription.label',default:'jobDescription')}" />
    <lay:showElement value="${employeeInternalAssignation?.note}" type="String" label="${message(code:'employeeInternalAssignation.note.label',default:'note')}" />
</lay:showWidget>