
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="maritalStatusListEmployee" action="autocomplete" name="maritalStatusListEmployee.id" label="${message(code:'maritalStatusEmployeeNote.maritalStatusListEmployee.label',default:'maritalStatusListEmployee')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'maritalStatusEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'maritalStatusEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'maritalStatusEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
