
<el:formGroup>
    <el:integerField name="locationId" size="8"  class=" isNumber" label="${message(code:'provinceLocation.locationId.label',default:'locationId')}" />
    
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="province" action="autocomplete" name="province.id" label="${message(code:'provinceLocation.province.label',default:'province')}" />
</el:formGroup>
