
<g:render template="/DescriptionInfo/wrapper" model="[bean:jobCategory?.descriptionInfo]" />
<el:formGroup>
    <el:textArea name="description" size="8"  class="" label="${message(code:'jobCategory.description.label',default:'description')}" value="${jobCategory?.description}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'jobCategory.universalCode.label',default:'universalCode')}" value="${jobCategory?.universalCode}"/>
</el:formGroup>
