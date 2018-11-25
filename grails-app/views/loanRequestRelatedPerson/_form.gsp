
<el:formGroup>
    <el:dateField name="effectiveDate"  size="8" class=" isRequired" label="${message(code:'loanRequestRelatedPerson.effectiveDate.label',default:'effectiveDate')}" value="${loanRequestRelatedPerson?.effectiveDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'loanRequestRelatedPerson.firm.label',default:'firm')}" values="${[[loanRequestRelatedPerson?.firm?.id,loanRequestRelatedPerson?.firm?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="loanRequest" action="autocomplete" name="loanRequest.id" label="${message(code:'loanRequestRelatedPerson.loanRequest.label',default:'loanRequest')}" values="${[[loanRequestRelatedPerson?.loanRequest?.id,loanRequestRelatedPerson?.loanRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumPersonSource"  from="${ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource.values()}" name="recordSource" size="8"  class=" isRequired" label="${message(code:'loanRequestRelatedPerson.recordSource.label',default:'recordSource')}" value="${loanRequestRelatedPerson?.recordSource}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="requestedPersonId" size="8"  class=" isNumber" label="${message(code:'loanRequestRelatedPerson.requestedPersonId.label',default:'requestedPersonId')}" value="${loanRequestRelatedPerson?.requestedPersonId}" />
</el:formGroup>