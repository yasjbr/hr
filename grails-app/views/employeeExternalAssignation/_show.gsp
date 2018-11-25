<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeeExternalAssignation?.employmentRecord}" type="EmploymentRecord" label="${message(code:'employeeExternalAssignation.employmentRecord.label',default:'employmentRecord')}" />
    <lay:showElement value="${employeeExternalAssignation?.assignedToOrganizationFromDate}" type="ZonedDateTime" label="${message(code:'employeeExternalAssignation.assignedToOrganizationFromDate.label',default:'assignedToOrganizationFromDate')}" />
    <lay:showElement value="${employeeExternalAssignation?.transientData?.organizationDTO?.toString()}" type="Long" label="${message(code:'employeeExternalAssignation.assignedToOrganizationId.label',default:'assignedToOrganizationId')}" />
    <lay:showElement value="${employeeExternalAssignation?.note}" type="String" label="${message(code:'employeeExternalAssignation.note.label',default:'note')}" />
</lay:showWidget>