
<g:if test='${fieldName == null || fieldName.isEmpty()}'>
    <g:set var="fieldName" value="location"></g:set>
</g:if>
<g:if test='${isRequired == null}'>
    <g:set var="isRequired" value=" isRequired"></g:set>
</g:if>
<g:if test='${isCountryRequired}'>
    <g:set var="isCountryRequired" value=" isRequired"></g:set>
</g:if>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="regionAutoComplete"
            name="region.id"
            label="${message(code:'location.region.label',default:'region')}"
            values="${[[location?.region?.id, location?.region?.descriptionInfo?.localName]]}" />

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" ${isCountryRequired}"
            controller="pcore"
            action="countryAutoComplete"
            name="country.id"
            label="${message(code:'location.country.label',default:'country')}"
            values="${[[location?.country?.id, location?.country?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="districtAutoComplete"
            name="district.id"
            label="${message(code:'location.district.label',default:'district')}"
            values="${[[location?.district?.id, location?.district?.descriptionInfo?.localName]]}" />
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="governorateAutoComplete"
            name="governorate.id"
            label="${message(code:'location.governorate.label',default:'governorate')}"
            values="${[[location?.governorate?.id, location?.governorate?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="localityAutoComplete"
            name="locality.id"
            label="${message(code:'location.locality.label',default:'locality')}"
            values="${[[location?.locality?.id, location?.locality?.descriptionInfo?.localName]]}" />
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="pcore"
            action="blockAutoComplete"
            name="block.id"
            label="${message(code:'location.block.label',default:'block')}"
            values="${[[location?.block?.id, location?.block?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="streetAutoComplete"
            name="street.id"
            label="${message(code:'location.street.label',default:'street')}"
            values="${[[location?.street?.id, location?.street?.descriptionInfo?.localName]]}" />
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class="" controller="pcore"
            action="buildingAutoComplete"
            name="building.id"
            label="${message(code:'location.building.label',default:'building')}"
            values="${[[location?.building?.id, location?.building?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="pcore"
            action="areaClassAutoComplete"
            name="areaClass.id"
            label="${message(code:'location.areaClass.label',default:'areaClass')}"
            values="${[[location?.areaClass?.id, location?.areaClass?.descriptionInfo?.localName]]}" />
</el:formGroup>

<g:render template="/pcore/location/script" />