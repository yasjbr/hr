
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'petitionListEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'petitionListEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'petitionListEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="petitionListEmployee" action="autocomplete" name="petitionListEmployee.id" label="${message(code:'petitionListEmployeeNote.petitionListEmployee.label',default:'petitionListEmployee')}" />
</el:formGroup>
