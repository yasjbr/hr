<g:render template="/pcore/person/wrapper" model="[bean:personDisabilityInfo?.person,isSearch:true]" />
<el:formGroup>
    <el:checkboxField name="accommodationNeeded" size="8"  class=" " label="${message(code:'personDisabilityInfo.accommodationNeeded.label',default:'accommodationNeeded')}" value="${personDisabilityInfo?.accommodationNeeded}" isChecked="${personDisabilityInfo?.accommodationNeeded}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="disabilityLevel" action="autocomplete" name="disabilityLevel.id" label="${message(code:'personDisabilityInfo.disabilityLevel.label',default:'disabilityLevel')}" values="${[[personDisabilityInfo?.disabilityLevel?.id,personDisabilityInfo?.disabilityLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="disabilityType" action="autocomplete" name="disabilityType.id" label="${message(code:'personDisabilityInfo.disabilityType.label',default:'disabilityType')}" values="${[[personDisabilityInfo?.disabilityType?.id,personDisabilityInfo?.disabilityType?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:decimalField name="percentage" size="8"  class="  isDecimal" label="${message(code:'personDisabilityInfo.percentage.label',default:'percentage')}"  value="${personDisabilityInfo?.percentage}" />
</el:formGroup>

