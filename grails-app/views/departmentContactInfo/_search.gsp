
<el:formGroup>
    <el:integerField name="contactMethodId" size="8"  class=" isNumber" label="${message(code:'departmentContactInfo.contactMethodId.label',default:'contactMethodId')}" />
    
</el:formGroup>
<el:formGroup>
    <el:integerField name="contactTypeId" size="8"  class=" isNumber" label="${message(code:'departmentContactInfo.contactTypeId.label',default:'contactTypeId')}" />
    
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="department" action="autocomplete" name="department.id" label="${message(code:'departmentContactInfo.department.label',default:'department')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'departmentContactInfo.fromDate.label',default:'fromDate')}" />
</el:formGroup>
<g:render template="/location/wrapper" model="[bean:departmentContactInfo?.locationId,isSearch:true]" />
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'departmentContactInfo.toDate.label',default:'toDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="value" size="8"  class="" label="${message(code:'departmentContactInfo.value.label',default:'value')}" />
</el:formGroup>
