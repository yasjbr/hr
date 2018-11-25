
<g:render template="/DescriptionInfo/wrapper" model="[bean:employeeStatusCategory?.descriptionInfo]" />

<el:formGroup>
    <el:textArea name="description" size="8"  class="" label="${message(code:'employeeStatusCategory.description.label',default:'description')}" value="${employeeStatusCategory?.description}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'employeeStatusCategory.universalCode.label',default:'universalCode')}" value="${employeeStatusCategory?.universalCode}"/>
</el:formGroup>