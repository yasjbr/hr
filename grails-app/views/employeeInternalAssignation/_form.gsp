<g:hiddenField name="employee.id" value="${employeeInternalAssignation?.employmentRecord?.employee?.id}" />

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="department" action="autocomplete" name="assignedToDepartment.id" label="${message(code:'employeeInternalAssignation.assignedToDepartment.label',default:'assignedToDepartment')}" values="${[[employeeInternalAssignation?.assignedToDepartment?.id,employeeInternalAssignation?.assignedToDepartment?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="assignedToDepartmentFromDate" setMinDateFor="assignedToDepartmentToDate" size="8" class=" isRequired" label="${message(code:'employeeInternalAssignation.assignedToDepartmentFromDate.label',default:'assignedToDepartmentFromDate')}" value="${employeeInternalAssignation?.assignedToDepartmentFromDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="assignedToDepartmentToDate"  size="8" class=" " label="${message(code:'employeeInternalAssignation.assignedToDepartmentToDate.label',default:'assignedToDepartmentFromDate')}" value="${employeeInternalAssignation?.assignedToDepartmentToDate}" />
</el:formGroup>

<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'employeeInternalAssignation.note.label',default:'note')}" value="${employeeInternalAssignation?.note}"/>
</el:formGroup>


