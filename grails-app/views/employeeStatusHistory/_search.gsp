
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'employeeStatusHistory.employee.label',default:'employee')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employeeStatus" action="autocomplete" name="employeeStatus.id" label="${message(code:'employeeStatusHistory.employeeStatus.label',default:'employeeStatus')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'employeeStatusHistory.fromDate.label',default:'fromDate')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'employeeStatusHistory.toDate.label',default:'toDate')}" />
</el:formGroup>
