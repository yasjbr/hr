<g:hiddenField name="employee.id" value="${employeeExternalAssignation?.employmentRecord?.employee?.id}" />
<el:formGroup>


    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="organization" action="autocomplete" name="assignedToOrganizationId"
                     label="${message(code: 'employmentRecord.assignedToOrganizationId.label', default: 'assignedToOrganization')}"
                     values="${[[employeeExternalAssignation?.assignedToOrganizationId,
                                 employeeExternalAssignation?.transientData?.organizationDTO?.toString()]]}" />

</el:formGroup>

<el:formGroup>
    <el:dateField name="assignedToOrganizationFromDate"  size="8" class=" isRequired" label="${message(code:'employeeExternalAssignation.assignedToOrganizationFromDate.label',default:'assignedToOrganizationFromDate')}" value="${employeeExternalAssignation?.assignedToOrganizationFromDate}" />
</el:formGroup>


<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'employeeExternalAssignation.note.label',default:'note')}" value="${employeeExternalAssignation?.note}"/>
</el:formGroup>