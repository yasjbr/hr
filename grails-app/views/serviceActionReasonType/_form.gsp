
<g:render template="/DescriptionInfo/wrapper" model="[bean:serviceActionReasonType?.descriptionInfo]" />
<el:formGroup>
    <el:checkboxField name="isRelatedToEndOfService" size="8"  class="" label="${message(code:'serviceActionReasonType.isRelatedToEndOfService.label',default:'isRelatedToEndOfService')}" value="${serviceActionReasonType?.isRelatedToEndOfService}" isChecked="${serviceActionReasonType?.isRelatedToEndOfService}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'serviceActionReasonType.universalCode.label',default:'universalCode')}" value="${serviceActionReasonType?.universalCode}"/>
</el:formGroup>