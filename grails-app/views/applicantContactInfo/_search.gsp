
<el:formGroup>
    <el:integerField name="contactMethodId" size="8"  class=" isNumber" label="${message(code:'contactInfo.contactMethod.label',default:'contactMethodId')}" />
    
</el:formGroup>
<el:formGroup>
    <el:integerField name="contactTypeId" size="8"  class=" isNumber" label="${message(code:'contactInfo.contactType.label',default:'contactTypeId')}" />
    
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="applicant" action="autocomplete" name="applicant.id" label="${message(code:'contactInfo.applicant.label',default:'applicant')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'contactInfo.fromDate.label',default:'fromDate')}" />
</el:formGroup>
<g:render template="/location/wrapper" model="[bean:applicantContactInfo?.locationId,isSearch:true]" />
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'contactInfo.toDate.label',default:'toDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="value" size="8"  class="" label="${message(code:'contactInfo.value.label',default:'value')}" />
</el:formGroup>
