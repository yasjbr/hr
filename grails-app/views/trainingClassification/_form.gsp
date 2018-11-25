<el:formGroup>
    <el:textField name="code" size="8"  class=" isRequired" label="${message(code:'trainingClassification.code.label',default:'code')}" value="${trainingClassification?.code}"/>
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:trainingClassification?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'trainingClassification.universalCode.label',default:'universalCode')}" value="${trainingClassification?.universalCode}"/>
</el:formGroup>