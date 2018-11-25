<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="pcore" action="regionAutoComplete" id="regionId" name="region.id" label="${message(code:'location.region.label',default:'region')}" values="${[[departmentContactInfo?.transientData?.regionId, departmentContactInfo?.transientData?.regionName?:'']]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" paramsGenerateFunction="countryParams" class=" isRequired" id="countryId" controller="pcore" action="countryAutoComplete" name="country.id" label="${message(code:'location.country.label',default:'country')}" values="${[[departmentContactInfo?.transientData?.countryId, departmentContactInfo?.transientData?.countryName?:'']]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"  paramsGenerateFunction="districtParams" id="districtId" class="" controller="pcore" action="districtAutoComplete" name="district.id" label="${message(code:'location.district.label',default:'district')}" values="${[[departmentContactInfo?.transientData?.districtId,departmentContactInfo?.transientData?.districtName?:'']]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" paramsGenerateFunction="governorateParams" id="governorateId" class=" isRequired" controller="pcore" action="governorateAutoComplete" name="governorate.id" label="${message(code:'location.governorate.label',default:'governorate')}" values="${[[departmentContactInfo?.transientData?.governorateId,departmentContactInfo?.transientData?.governorateName?:'']]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" paramsGenerateFunction="localityParams"  id="localityId" class=" isRequired" controller="pcore" action="localityAutoComplete" name="locality.id" label="${message(code:'location.locality.label',default:'locality')}" values="${[[departmentContactInfo?.transientData?.localityId,departmentContactInfo?.transientData?.localityName?:'']]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" paramsGenerateFunction="blockParam"  id="blockId" class=" " controller="pcore" action="blockAutoComplete" name="block.id" label="${message(code:'location.block.label',default:'block')}" values="${[[departmentContactInfo?.transientData?.blockId, departmentContactInfo?.transientData?.blockName?:'']]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" paramsGenerateFunction="streetParam" id="streetId" class="" controller="pcore" action="streetAutoComplete" name="street.id" label="${message(code:'location.street.label',default:'street')}" values="${[[departmentContactInfo?.transientData?.streetId,departmentContactInfo?.transientData?.streetName?:'']]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"   paramsGenerateFunction="buildingParam" class="" controller="pcore" action="buildingAutoComplete" id="buildingId" name="building.id" label="${message(code:'location.building.label',default:'building')}" values="${[[departmentContactInfo?.transientData?.buildingId,departmentContactInfo?.transientData?.buildingName?:'']]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="pcore" action="areaClassAutoComplete" id="areaClassId" name="areaClass.id" label="${message(code:'location.areaClass.label',default:'areaClass')}" values="${[[departmentContactInfo?.transientData?.areaClassId,departmentContactInfo?.transientData?.areaClassName?:'']]}" />
</el:formGroup>


<script>
    function governorateParams() {
        var countryId = $('#countryId').val();
        var districtId = $('#districtId').val();
        var regionId=$('#regionId').val();
        return {'country.id': countryId,'district.id':districtId,'region.id':regionId}
    }

    function districtParams() {
        var countryId = $('#countryId').val();
        return {'country.id': countryId}
    }

    function localityParams() {
        var governorateId = $('#governorateId').val();
        return {'governorate.id': governorateId}
    }

    function blockParam() {
        var localityId = $('#localityId').val();
        var governorateId = $('#governorateId').val();
        return {'locality.id': localityId,'governorate.id': governorateId}
    }

    function streetParam() {
        var localityId = $('#localityId').val();
        return {'locality.id': localityId}
    }

    function buildingParam() {
        var localityId = $('#localityId').val();
        var streetId = $('#streetId').val();
        return {'locality.id': localityId,'street.id':streetId}
    }

    function resetDistrict() {
        gui.autocomplete.clear("districtId");
    }

    function resetGovernorate() {
        gui.autocomplete.clear("governorateId");
    }

    function resetLocality() {
        gui.autocomplete.clear("localityId");
    }


    function resetStreet() {
        gui.autocomplete.clear("streetId");
    }

    function resetBlock() {
        gui.autocomplete.clear("blockId");
    }

    function resetBuilding() {
        gui.autocomplete.clear("buildingId");
    }

    $("#countryId").on("select2:close", function (e) {
        resetDistrict();
        resetGovernorate();
        resetLocality();
        resetBlock();
        resetStreet();
    });
    $("#streetId").on("select2:close", function (e) {
        resetBuilding();
    });

</script>