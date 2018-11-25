
<g:render template="/DescriptionInfo/wrapper" model="[bean:militaryRankClassification?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'militaryRankClassification.universalCode.label',default:'universalCode')}" value="${militaryRankType?.universalCode}"/>
</el:formGroup>