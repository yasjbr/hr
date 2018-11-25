
<g:render template="/DescriptionInfo/wrapper" model="[bean:militaryRank?.descriptionInfo]" />
<el:formGroup>
    <el:integerField name="orderNo" size="8" maxlength="2" class=" isRequired isNumber" label="${message(code:'militaryRank.orderNo.label',default:'orderNo')}" value="${militaryRank?.orderNo}" />
</el:formGroup>

<el:formGroup>
    <el:integerField name="numberOfYearToPromote" size="8"  class=" isNumber" label="${message(code:'militaryRank.numberOfYearToPromote.label',default:'numberOfYearToPromote')}" value="${militaryRank?.numberOfYearToPromote}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'militaryRank.universalCode.label',default:'universalCode')}" value="${militaryRank?.universalCode}"/>
</el:formGroup>