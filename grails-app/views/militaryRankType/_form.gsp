
<g:render template="/DescriptionInfo/wrapper" model="[bean:militaryRankType?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'militaryRankType.universalCode.label',default:'universalCode')}" value="${militaryRankType?.universalCode}"/>
</el:formGroup>