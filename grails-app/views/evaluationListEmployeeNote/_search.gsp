
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="evaluationListEmployee" action="autocomplete" name="evaluationListEmployee.id" label="${message(code:'evaluationListEmployeeNote.evaluationListEmployee.label',default:'evaluationListEmployee')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'evaluationListEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'evaluationListEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'evaluationListEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
