
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="loanNominatedEmployee" action="autocomplete" name="loanNominatedEmployee.id" label="${message(code:'loanNominatedEmployeeNote.loanNominatedEmployee.label',default:'loanNominatedEmployee')}" values="${[[loanNominatedEmployeeNote?.loanNominatedEmployee?.id,loanNominatedEmployeeNote?.loanNominatedEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'loanNominatedEmployeeNote.note.label',default:'note')}" value="${loanNominatedEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'loanNominatedEmployeeNote.noteDate.label',default:'noteDate')}" value="${loanNominatedEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'loanNominatedEmployeeNote.orderNo.label',default:'orderNo')}" value="${loanNominatedEmployeeNote?.orderNo}"/>
</el:formGroup>