
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="generalListEmployee" action="autocomplete" name="generalListEmployee.id" label="${message(code:'generalListEmployeeNote.generalListEmployee.label',default:'generalListEmployee')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'generalListEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'generalListEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'generalListEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
