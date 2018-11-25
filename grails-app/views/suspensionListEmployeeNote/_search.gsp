
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'suspensionListEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'suspensionListEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'suspensionListEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="suspensionListEmployee" action="autocomplete" name="suspensionListEmployee.id" label="${message(code:'suspensionListEmployeeNote.suspensionListEmployee.label',default:'suspensionListEmployee')}" />
</el:formGroup>
