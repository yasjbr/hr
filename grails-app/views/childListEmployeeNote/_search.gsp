
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="childListEmployee" action="autocomplete" name="childListEmployee.id" label="${message(code:'childListEmployeeNote.childListEmployee.label',default:'childListEmployee')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'childListEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'childListEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'childListEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
