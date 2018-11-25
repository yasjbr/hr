<el:hiddenField name="employee.id" value="${employeeViolation?.employee?.id}"/>

<g:render template="/employee/wrapperForm" model="[employee:employeeViolation?.employee]"  />

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "employeeViolation.info.label")}">
    <lay:widgetBody>


        <el:formGroup>
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             controller="disciplinaryCategory" action="autocomplete" name="disciplinaryCategoryId"
                             label="${message(code:'employeeViolation.disciplinaryCategory.label',default:'disciplinaryCategory')}"
                             values="${[[employeeViolation?.disciplinaryReason?.disciplinaryCategories?.id,
                                         employeeViolation?.disciplinaryReason?.disciplinaryCategories?.descriptionInfo?.localName]]}" />


            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired" paramsGenerateFunction="disciplinaryReasonParams"
                             controller="disciplinaryReason" action="autocomplete" id="disciplinaryReasonId" name="disciplinaryReason.id"
                             label="${message(code:'employeeViolation.disciplinaryReason.label',default:'disciplinaryReason')}"
                             values="${[[employeeViolation?.disciplinaryReason?.id,
                                         employeeViolation?.disciplinaryReason?.descriptionInfo?.localName]]}" />



        </el:formGroup>


        <el:formGroup>


            <el:dateField name="violationDate"  size="6" class=" isRequired" setMinDateFor="fromDate" isMaxDate="true"
                          label="${message(code:'employeeViolation.violationDate.label',default:'violationDate')}"
                          value="${employeeViolation?.violationDate}" />

            <el:dateField name="noticeDate"  size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code:'employeeViolation.noticeDate.label',default:'noticeDate')}"
                          value="${employeeViolation?.noticeDate}" />



        </el:formGroup>


        <el:formGroup>

            <g:render template="/employee/wrapper" model="[
                    name:'informer.id',
                    messageValue:message(code:'employeeViolation.informer.label'),
                    disableFormGroupName:true,
                    bean:employeeViolation?.informer,
                    isSearch: true,
                    size:6
            ]"/>


            <el:textArea name="note" size="6"
                         class=" " label="${message(code:'employeeViolation.note.label',default:'note')}"
                         value="${employeeViolation?.note}"/>
        </el:formGroup>




    </lay:widgetBody>
</lay:widget>

<lay:widget transparent="true" color="blue" icon="icon-location" title="${g.message(code: "employeeViolation.locationId.label")}">
    <lay:widgetBody>
        <el:hiddenField name="locationId" value="${employeeViolation?.locationId}"/>
        <g:render template="/pcore/location/staticWrapper"
                  model="[location          : employeeViolation?.transientData?.locationDTO,
                          isRequired        : false,
                          isRequiredFields  : false,
                          isRegionRequired  : false,
                          isCountryRequired : false,
                          isDistrictRequired: false]"/>
        <el:formGroup>
            <el:textArea name="unstructuredLocation" size="6" class=" "
                         label="${message(code: 'employeeViolation.unstructuredLocation.label', default: 'unstructuredLocation')}"
                         value="${employeeViolation?.unstructuredLocation}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>


<script>
    function disciplinaryReasonParams() {
        var disciplinaryCategoryId = $('#disciplinaryCategoryId').val();
        return {"disciplinaryCategory.id":disciplinaryCategoryId}
    }

    $("#disciplinaryCategoryId").on("select2:close", function (e) {
        var value = $('#disciplinaryReasonId').val();
        if(value){
            $('#disciplinaryReasonId').val("");
            $('#disciplinaryReasonId').trigger('change');
        }
    });

    $("#disciplinaryReasonId").on("select2:close", function (e) {
        var value = $('#disciplinaryReasonId').val();
        var disciplinaryCategoryValue = $('#disciplinaryCategoryId').val();
        if(value && !disciplinaryCategoryValue){
            $.ajax({
                url: '${createLink(controller: 'disciplinaryReason',action: 'getInstance')}',
                type: 'POST',
                data: {
                    id: value
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
                    if(json.disciplinaryCategories.id){
                        $("#disciplinaryCategoryId").val(json.disciplinaryCategories.id);
                        var newOption = new Option(json.disciplinaryCategories.descriptionInfo.localName,json.disciplinaryCategories.id, true, true);
                        $('#disciplinaryCategoryId').append(newOption);
                        $('#disciplinaryCategoryId').trigger('change');
                    }
                }
            });
        }
    });

</script>