
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="loanNominatedEmployee" action="autocomplete" name="loanNominatedEmployee.id" label="${message(code:'loanNominatedEmployeeNote.loanNominatedEmployee.label',default:'loanNominatedEmployee')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'loanNominatedEmployeeNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'loanNominatedEmployeeNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'loanNominatedEmployeeNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
