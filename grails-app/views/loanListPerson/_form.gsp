
<el:formGroup>
    <el:textArea name="description" size="8"  class="" label="${message(code:'loanListPerson.description.label',default:'description')}" value="${loanListPerson?.description}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="effectiveDate"  size="8" class=" isRequired" label="${message(code:'loanListPerson.effectiveDate.label',default:'effectiveDate')}" value="${loanListPerson?.effectiveDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'loanListPerson.firm.label',default:'firm')}" values="${[[loanListPerson?.firm?.id,loanListPerson?.firm?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" isRequired" label="${message(code:'loanListPerson.fromDate.label',default:'fromDate')}" value="${loanListPerson?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="isEmploymentProfileProvided" size="8"  class=" isRequired" label="${message(code:'loanListPerson.isEmploymentProfileProvided.label',default:'isEmploymentProfileProvided')}" value="${loanListPerson?.isEmploymentProfileProvided}" isChecked="${loanListPerson?.isEmploymentProfileProvided}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="loanList" action="autocomplete" name="loanList.id" label="${message(code:'loanListPerson.loanList.label',default:'loanList')}" values="${[[loanListPerson?.loanList?.id,loanListPerson?.loanList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="loanRequest" action="autocomplete" name="loanRequest.id" label="${message(code:'loanListPerson.loanRequest.label',default:'loanRequest')}" values="${[[loanListPerson?.loanRequest?.id,loanListPerson?.loanRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="periodInMonths" size="8"  class=" isRequired isNumber" label="${message(code:'loanListPerson.periodInMonths.label',default:'periodInMonths')}" value="${loanListPerson?.periodInMonths}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'loanListPerson.recordStatus.label',default:'recordStatus')}" value="${loanListPerson?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="requestedFromOrganizationId" size="8"  class=" isNumber" label="${message(code:'loanListPerson.requestedFromOrganizationId.label',default:'requestedFromOrganizationId')}" value="${loanListPerson?.requestedFromOrganizationId}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="requestedPersonId" size="8"  class=" isNumber" label="${message(code:'loanListPerson.requestedPersonId.label',default:'requestedPersonId')}" value="${loanListPerson?.requestedPersonId}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" isRequired" label="${message(code:'loanListPerson.toDate.label',default:'toDate')}" value="${loanListPerson?.toDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="department" action="autocomplete" name="toDepartment.id" label="${message(code:'loanListPerson.toDepartment.label',default:'toDepartment')}" values="${[[loanListPerson?.toDepartment?.id,loanListPerson?.toDepartment?.descriptionInfo?.localName]]}" />
</el:formGroup>