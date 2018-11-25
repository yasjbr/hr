<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'operationalTask.universalCode.label',default:'universalCode')}" value="${operationalTask?.universalCode}"/>
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:operationalTask?.descriptionInfo]" />
