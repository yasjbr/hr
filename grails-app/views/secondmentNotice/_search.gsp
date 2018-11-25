
<el:formGroup>
    <el:textArea name="description" size="8"  class="" label="${message(code:'secondmentNotice.description.label',default:'description')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'secondmentNotice.firm.label',default:'firm')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'secondmentNotice.fromDate.label',default:'fromDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="jobTitle" size="8"  class="" label="${message(code:'secondmentNotice.jobTitle.label',default:'jobTitle')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="militaryRank" action="autocomplete" name="militaryRank.id" label="${message(code:'secondmentNotice.militaryRank.label',default:'militaryRank')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'secondmentNotice.orderNo.label',default:'orderNo')}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="periodInMonths" size="8"  class=" isNumber" label="${message(code:'secondmentNotice.periodInMonths.label',default:'periodInMonths')}" />
    
</el:formGroup>
<el:formGroup>
    <el:integerField name="requesterOrganizationId" size="8"  class=" isNumber" label="${message(code:'secondmentNotice.requesterOrganizationId.label',default:'requesterOrganizationId')}" />
    
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'secondmentNotice.toDate.label',default:'toDate')}" />
</el:formGroup>
