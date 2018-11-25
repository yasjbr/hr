
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'vacationListEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'vacationListEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'vacationListEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="vacationListEmployee" action="autocomplete" name="vacationListEmployee.id" label="${message(code:'vacationListEmployeeNote.vacationListEmployee.label',default:'vacationListEmployee')}" />
</el:formGroup>
