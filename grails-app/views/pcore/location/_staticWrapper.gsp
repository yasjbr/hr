<g:if test='${fieldName == null || fieldName.isEmpty()}'>
    <g:set var="fieldName" value="location"></g:set>
</g:if>
<g:if test='${isRequired == null}'>
    <g:set var="isRequired" value="isRequired"></g:set>
</g:if>
<g:if test='${size == null}'>
    <g:set var="size" value="6"></g:set>
</g:if>

<g:if test='${isRequiredFields == false}'>
    <g:set var="isRequiredFields" value="" />
</g:if>
<g:else>
    <g:set var="isRequiredFields" value="isRequired" />
</g:else>

<g:if test='${isRegionRequired}'>
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="${size}"
                class=""
                controller="pcore"
                action="regionAutoComplete"
                name="regionId"
                label="${message(code:'location.region.label',default:'region')}"
                values="${[[location?.region?.id, location?.region?.descriptionInfo?.localName]]}" />
    </el:formGroup>
</g:if>

<g:if test='${isCountryRequired}'>
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="${size}"
                class=" isRequired"
                controller="pcore"
                action="countryAutoComplete"
                name="countryId"
                label="${message(code:'location.country.label',default:'country')}"
                values="${[[location?.country?.id, location?.country?.descriptionInfo?.localName]]}" />
        <g:if test="${hiddenDetails}">
            <g:render template="/pcore/location/detailsDiv" />
        </g:if>
    </el:formGroup>
</g:if>

<g:if test='${isDistrictRequired}'>
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="${size}"
                class=""
                paramsGenerateFunction="districtParams"
                controller="pcore"
                action="districtAutoComplete"
                name="districtId"
                label="${message(code:'location.district.label',default:'district')}"
                values="${[[location?.district?.id, location?.district?.descriptionInfo?.localName]]}" />
    </el:formGroup>
</g:if>


<g:if test='${hiddenDetails}'>
    <div id="locationDetails" style="display: none;">
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="${size}"
                class=""
                controller="pcore"
                paramsGenerateFunction="governorateParams"
                action="governorateAutoComplete"
                name="governorateId"
                label="${message(code:'location.governorate.label',default:'governorate')}"
                values="${[[location?.governorate?.id, location?.governorate?.descriptionInfo?.localName]]}" />
    </el:formGroup>

    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="${size}"
                class=""
                controller="pcore"
                action="localityAutoComplete"
                paramsGenerateFunction="localityParams"
                name="localityId"
                label="${message(code:'location.locality.label',default:'locality')}"
                values="${[[location?.locality?.id, location?.locality?.descriptionInfo?.localName]]}" />
    </el:formGroup>
</g:if>
<g:else>
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="${size}"
                class=" ${isRequiredFields}"
                controller="pcore"
                paramsGenerateFunction="governorateParams"
                action="governorateAutoComplete"
                name="governorateId"
                label="${message(code:'location.governorate.label',default:'governorate')}"
                values="${[[location?.governorate?.id, location?.governorate?.descriptionInfo?.localName]]}" />
    </el:formGroup>

    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="${size}"
                class=" ${isRequiredFields}"
                controller="pcore"
                action="localityAutoComplete"
                paramsGenerateFunction="localityParams"
                name="localityId"
                label="${message(code:'location.locality.label',default:'locality')}"
                values="${[[location?.locality?.id, location?.locality?.descriptionInfo?.localName]]}" />

        <g:render template="/pcore/location/detailsDiv" />

    </el:formGroup>

    <div id="locationDetails" style="display: none;">
</g:else>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="${size}"
            class=" "
            controller="pcore"
            paramsGenerateFunction="blockParam"
            action="blockAutoComplete"
            name="blockId"
            label="${message(code:'location.block.label',default:'block')}"
            values="${[[location?.block?.id, location?.block?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="${size}"
            class=""
            controller="pcore"
            action="streetAutoComplete"
            name="streetId"
            paramsGenerateFunction="streetParam"
            label="${message(code:'location.street.label',default:'street')}"
            values="${[[location?.street?.id, location?.street?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="${size}"
            class="" controller="pcore"
            action="buildingAutoComplete"
            name="buildingId"
            paramsGenerateFunction="buildingParam"
            label="${message(code:'location.building.label',default:'building')}"
            values="${[[location?.building?.id, location?.building?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="${size}"
            class=""
            controller="pcore"
            action="areaClassAutoComplete"
            name="areaClassId"
            label="${message(code:'location.areaClass.label',default:'areaClass')}"
            values="${[[location?.areaClass?.id, location?.areaClass?.descriptionInfo?.localName]]}" />
</el:formGroup>
</div>
<g:render template="/pcore/location/script" />
