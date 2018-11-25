
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'returnFromAbsenceListEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'returnFromAbsenceListEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'returnFromAbsenceListEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="returnFromAbsenceListEmployee" action="autocomplete" name="returnFromAbsenceListEmployee.id" label="${message(code:'returnFromAbsenceListEmployeeNote.returnFromAbsenceListEmployee.label',default:'returnFromAbsenceListEmployee')}" />
</el:formGroup>
