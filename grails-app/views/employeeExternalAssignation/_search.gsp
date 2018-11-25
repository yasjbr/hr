
<el:formGroup>
    <el:dateField name="assignedToOrganizationFromDate"  size="8" class="" label="${message(code:'employeeExternalAssignation.assignedToOrganizationFromDate.label',default:'assignedToOrganizationFromDate')}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="assignedToOrganizationId" size="8"  class=" isNumber" label="${message(code:'employeeExternalAssignation.assignedToOrganizationId.label',default:'assignedToOrganizationId')}" />
    
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employmentRecord" action="autocomplete" name="employmentRecord.id" label="${message(code:'employeeExternalAssignation.employmentRecord.label',default:'employmentRecord')}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'employeeExternalAssignation.note.label',default:'note')}" />
</el:formGroup>
