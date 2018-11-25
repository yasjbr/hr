
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="loanListPerson" action="autocomplete" name="loanListPerson.id" label="${message(code:'loanListPersonNote.loanListPerson.label',default:'loanListPerson')}" values="${[[loanListPersonNote?.loanListPerson?.id,loanListPersonNote?.loanListPerson?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'loanListPersonNote.note.label',default:'note')}" value="${loanListPersonNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'loanListPersonNote.noteDate.label',default:'noteDate')}" value="${loanListPersonNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'loanListPersonNote.orderNo.label',default:'orderNo')}" value="${loanListPersonNote?.orderNo}"/>
</el:formGroup>