<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=""
                     controller="country" action="autocomplete" name="location.country.id"
                     label="${message(code:'country.label',default:'country')}"
                     values="${[[location?.country?.id,
                                 location?.country?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"  controller="governorate" action="autocomplete" name="location.governorate.id" label="${message(code:'governorate.label',default:'location')}" values="${[[location?.governorate?.id,location?.governorate?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"  controller="locality" action="autocomplete" name="location.locality.id" label="${message(code:'locality.label',default:'location')}" values="${[[location?.locality?.id,location?.locality?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"  controller="block" action="autocomplete" name="location.block.id" label="${message(code:'block.label',default:'location')}" values="${[[location?.block?.id,location?.block?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"  controller="street" action="autocomplete" name="location.street.id" label="${message(code:'street.label',default:'location')}" values="${[[location?.street?.id,location?.street?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"  controller="building" action="autocomplete" name="location.building.id" label="${message(code:'building.label',default:'location')}" values="${[[location?.building?.id,location?.building?.descriptionInfo?.localName]]}" />
</el:formGroup>