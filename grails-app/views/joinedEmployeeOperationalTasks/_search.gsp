
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'joinedEmployeeOperationalTasks.employee.label',default:'employee')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'joinedEmployeeOperationalTasks.fromDate.label',default:'fromDate')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="operationalTask" action="autocomplete" name="operationalTask.id" label="${message(code:'joinedEmployeeOperationalTasks.operationalTask.label',default:'operationalTask')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'joinedEmployeeOperationalTasks.toDate.label',default:'toDate')}" />
</el:formGroup>
