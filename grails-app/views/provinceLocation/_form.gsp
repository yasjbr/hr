
<el:formGroup>
    <el:integerField name="locationId" size="8"  class=" isRequired isNumber" label="${message(code:'provinceLocation.locationId.label',default:'locationId')}" value="${provinceLocation?.locationId}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="province" action="autocomplete" name="province.id" label="${message(code:'provinceLocation.province.label',default:'province')}" values="${[[provinceLocation?.province?.id,provinceLocation?.province?.descriptionInfo?.localName]]}" />
</el:formGroup>