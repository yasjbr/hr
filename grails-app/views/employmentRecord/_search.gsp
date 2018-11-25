
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="department" action="autocomplete" name="department.id" label="${message(code:'employmentRecord.department.label',default:'department')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'employmentRecord.employee.label',default:'employee')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employmentCategory" action="autocomplete" name="employmentCategory.id" label="${message(code:'employmentRecord.employmentCategory.label',default:'employmentCategory')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'employmentRecord.fromDate.label',default:'fromDate')}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="jobDescription" size="8"  class="" label="${message(code:'employmentRecord.jobDescription.label',default:'jobDescription')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="jobTitle" action="autocomplete" name="jobTitle.id" label="${message(code:'employmentRecord.jobTitle.label',default:'jobTitle')}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'employmentRecord.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'employmentRecord.toDate.label',default:'toDate')}" />
</el:formGroup>
