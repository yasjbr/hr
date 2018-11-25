
<g:render template="/DescriptionInfo/wrapper" model="[bean:disciplinaryCategory?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'disciplinaryCategory.universalCode.label',default:'universalCode')}" value="${disciplinaryCategory?.universalCode}"/>
</el:formGroup>
