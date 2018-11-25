
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="department" action="autocomplete" name="assignedToDepartment.id" label="${message(code:'employeeInternalAssignation.assignedToDepartment.label',default:'assignedToDepartment')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="assignedToDepartmentFromDate"  size="8" class="" label="${message(code:'employeeInternalAssignation.assignedToDepartmentFromDate.label',default:'assignedToDepartmentFromDate')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employmentRecord" action="autocomplete" name="employmentRecord.id" label="${message(code:'employeeInternalAssignation.employmentRecord.label',default:'employmentRecord')}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'employeeInternalAssignation.note.label',default:'note')}" />
</el:formGroup>
