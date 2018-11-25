<script>

    function showLocationDetails() {
        $("#locationDetails").toggle();
        $("#showDetails").toggle();
        $("#hideDetails").toggle();
    }
    function hideLocationDetails() {
        $("#locationDetails").toggle();
        $("#showDetails").toggle();
        $("#hideDetails").toggle();
    }

    function governorateParams() {
        var countryId = $('#countryId').val();
        var districtId = $('#districtId').val();
        return {'country.id': countryId,'district.id':districtId}
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
        return {'locality.id': localityId}
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
    $("#districtId").on("select2:close", function (e) {
        resetGovernorate();
        resetLocality();
        resetBlock();
        resetStreet();
        var value = $('#districtId').val();
        if(value){
            $.ajax({
                url: '${createLink(controller: 'location',action: 'getLocationInfo')}',
                type: 'POST',
                data: {
                    id: value,
                    entityName: "district"
                },
                dataType: 'json',
                beforeSend: function(jqXHR,settings) {
                    guiLoading.show();
                },
                error: function(jqXHR) {
                    guiLoading.hide();
                },
                success: function(json) {
                    guiLoading.hide();

                    $("#countryId").val(json.data.country.id);
                    var newOption = new Option(json.data.country.descriptionInfo.localName,json.data.country.id, true, true);
                    $('#countryId').append(newOption);
                    $('#countryId').trigger('change');

                }
            });
        }

    });
    $("#governorateId").on("select2:close", function (e) {
        resetLocality();
        resetBlock();
        resetStreet();
        var value = $('#governorateId').val();
        if(value){
            $.ajax({
                url: '${createLink(controller: 'location',action: 'getLocationInfo')}',
                type: 'POST',
                data: {
                    id: value,
                    entityName: "governorate"
                },
                dataType: 'json',
                beforeSend: function(jqXHR,settings) {
                    guiLoading.show();
                },
                error: function(jqXHR) {
                    guiLoading.hide();
                },
                success: function(json) {
                    guiLoading.hide();

                    $("#districtId").val(json.data.district.id);
                    var newOption = new Option(json.data.district.descriptionInfo.localName,json.data.district.id, true, true);
                    $('#districtId').append(newOption);
                    $('#districtId').trigger('change');

                    $("#countryId").val(json.data.district.country.id);
                    var newOption = new Option(json.data.district.country.descriptionInfo.localName,json.data.district.country.id, true, true);
                    $('#countryId').append(newOption);
                    $('#countryId').trigger('change');

                }
            });
        }

    });
    $("#localityId").on("select2:close", function (e) {
        resetBlock();
        resetStreet();
        var value = $('#localityId').val();
        if(value){
            $.ajax({
                url: '${createLink(controller: 'location',action: 'getLocationInfo')}',
                type: 'POST',
                data: {
                    id: value,
                    entityName: "locality"
                },
                dataType: 'json',
                beforeSend: function(jqXHR,settings) {
                    guiLoading.show();
                },
                error: function(jqXHR) {
                    guiLoading.hide();
                },
                success: function(json) {
                    guiLoading.hide();
                    $("#governorateId").val(json.data.governorate.id);
                    var newOption = new Option(json.data.governorate.descriptionInfo.localName,json.data.governorate.id, true, true);
                    $('#governorateId').append(newOption);
                    $('#governorateId').trigger('change');


                    $("#districtId").val(json.data.governorate.district.id);
                    var newOption = new Option(json.data.governorate.district.descriptionInfo.localName,json.data.governorate.district.id, true, true);
                    $('#districtId').append(newOption);
                    $('#districtId').trigger('change');

                    $("#countryId").val(json.data.governorate.district.country.id);
                    var newOption = new Option(json.data.governorate.district.country.descriptionInfo.localName,json.data.governorate.district.country.id, true, true);
                    $('#countryId').append(newOption);
                    $('#countryId').trigger('change');

                }
            });
        }

    });

    $("#blockId").on("select2:close", function (e) {
        resetStreet();
        var value = $('#blockId').val();
        if(value){
            $.ajax({
                url: '${createLink(controller: 'location',action: 'getLocationInfo')}',
                type: 'POST',
                data: {
                    id: value,
                    entityName: "block"
                },
                dataType: 'json',
                beforeSend: function(jqXHR,settings) {
                    guiLoading.show();
                },
                error: function(jqXHR) {
                    guiLoading.hide();
                },
                success: function(json) {
                    guiLoading.hide();

                    $("#localityId").val(json.data.locality.id);
                    var newOption = new Option(json.data.locality.descriptionInfo.localName,json.data.locality.id, true, true);
                    $('#localityId').append(newOption);
                    $('#localityId').trigger('change');

                    $("#governorateId").val(json.data.locality.governorate.id);
                    var newOption = new Option(json.data.locality.governorate.descriptionInfo.localName,json.data.locality.governorate.id, true, true);
                    $('#governorateId').append(newOption);
                    $('#governorateId').trigger('change');

                    $("#districtId").val(json.data.locality.governorate.district.id);
                    var newOption = new Option(json.data.locality.governorate.district.descriptionInfo.localName,json.data.locality.governorate.district.id, true, true);
                    $('#districtId').append(newOption);
                    $('#districtId').trigger('change');


                    $("#countryId").val(json.data.locality.governorate.district.country.id);
                    var newOption = new Option(json.data.locality.governorate.district.country.descriptionInfo.localName,json.data.locality.governorate.district.country.id, true, true);
                    $('#countryId').append(newOption);
                    $('#countryId').trigger('change');

                }
            });
        }

    });


    $("#streetId").on("select2:close", function (e) {
        resetBuilding();
    });

</script>