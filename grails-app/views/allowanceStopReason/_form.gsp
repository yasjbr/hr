
<g:render template="/DescriptionInfo/wrapper" model="[bean:allowanceStopReason?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'allowanceStopReason.universalCode.label',default:'universalCode')}" value="${allowanceStopReason?.universalCode}"/>
</el:formGroup>