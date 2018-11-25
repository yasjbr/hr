
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'suspensionExtensionListEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'suspensionExtensionListEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'suspensionExtensionListEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="suspensionExtensionListEmployee" action="autocomplete" name="suspensionExtinsionListEmployee.id" label="${message(code:'suspensionExtensionListEmployeeNote.suspensionExtinsionListEmployee.label',default:'suspensionExtinsionListEmployee')}" />
</el:formGroup>
