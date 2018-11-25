
<el:formGroup>
    
    <el:textField name="email" size="8"  class="" label="${message(code:'firmSupportContactInfo.email.label',default:'email')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="faxNumber" size="8"  class="" label="${message(code:'firmSupportContactInfo.faxNumber.label',default:'faxNumber')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'firmSupportContactInfo.firm.label',default:'firm')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="name" size="8"  class="" label="${message(code:'firmSupportContactInfo.name.label',default:'name')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="phoneNumber" size="8"  class="" label="${message(code:'firmSupportContactInfo.phoneNumber.label',default:'phoneNumber')}" />
</el:formGroup>
