
<g:render template="/DescriptionInfo/wrapper" model="[bean:trainingRejectionReason?.descriptionInfo]" />

<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'trainingRejectionReason.universalCode.label',default:'universalCode')}" value="${trainingRejectionReason?.universalCode}"/>
</el:formGroup>

<el:formGroup>
    <el:textArea name="description" size="8"  class="" label="${message(code:'trainingRejectionReason.description.label',default:'description')}" value="${trainingRejectionReason?.description}"/>
</el:formGroup>