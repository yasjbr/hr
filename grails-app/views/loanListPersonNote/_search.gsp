
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="loanListPerson" action="autocomplete" name="loanListPerson.id" label="${message(code:'loanListPersonNote.loanListPerson.label',default:'loanListPerson')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="note" size="8"  class="" label="${message(code:'loanListPersonNote.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class="" label="${message(code:'loanListPersonNote.noteDate.label',default:'noteDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'loanListPersonNote.orderNo.label',default:'orderNo')}" />
</el:formGroup>
