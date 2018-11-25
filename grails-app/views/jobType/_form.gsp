
<g:render template="/DescriptionInfo/wrapper" model="[bean:jobType?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'jobType.universalCode.label',default:'universalCode')}" value="${jobType?.universalCode}"/>
</el:formGroup>