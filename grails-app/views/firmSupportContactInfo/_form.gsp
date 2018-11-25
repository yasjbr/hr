<el:hiddenField name="firm.id" value="${params.firm?.id}"/>
<el:formGroup>
    <el:textField name="name" size="8"  class=" isRequired" label="${message(code:'firmSupportContactInfo.name.label',default:'name')}" value="${firmSupportContactInfo?.name}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="phoneNumber" size="8"  class="" label="${message(code:'firmSupportContactInfo.phoneNumber.label',default:'phoneNumber')}" value="${firmSupportContactInfo?.phoneNumber}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="faxNumber" size="8"  class="" label="${message(code:'firmSupportContactInfo.faxNumber.label',default:'faxNumber')}" value="${firmSupportContactInfo?.faxNumber}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="email" size="8"  class="" label="${message(code:'firmSupportContactInfo.email.label',default:'email')}" value="${firmSupportContactInfo?.email}"/>
</el:formGroup>


