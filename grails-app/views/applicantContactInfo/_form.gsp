<msg:page/>
<el:hiddenField name="personId" value="${applicant.personId}"/>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" isRequired"
                     controller="pcore"
                     action="contactTypeAutoComplete"
                     name="contactTypeId"
                     label="${message(code: 'contactInfo.contactType.label', default: 'Contact Type')}"
                     values="${[[applicantContactInfo?.contactType?.id, applicantContactInfo?.contactType?.descriptionInfo?.localName]]}"
                     id="contactTypeId"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" isRequired"
                     controller="pcore"
                     action="contactMethodAutoComplete"
                     name="contactMethodId"
                     paramsGenerateFunction="generateFilterParams"
                     label="${message(code: 'contactInfo.contactMethod.label', default: 'Contact Method')}"
                     values="${[[applicantContactInfo?.contactMethod?.id, applicantContactInfo?.contactMethod?.descriptionInfo?.localName]]}"/>
</el:formGroup>

    <div id="contactInfoLocationWrapperId">
        <lay:wall title="${g.message(code: 'contactInfo.address.label')}" id="addressWall">
            <el:hiddenField name="locationId" value="${applicantContactInfo?.address?.id}"/>
            <g:render template="/pcore/location/wrapper"
                      model="[location: applicantContactInfo?.address,
                              isRequired: true,
                              isRegionRequired: false,
                              isCountryRequired: false,
                              isDistrictRequired: false]"/>

        </lay:wall>
    </div>
    <el:formGroup>
        <el:textField name="value"
                      size="6"
                      class=" isRequired"
                      label="${message(code: 'contactInfo.value.label', default: 'value')}"
                      value="${applicantContactInfo?.value}"/>
    </el:formGroup>


    <script>
        var addressContactMethodsArray = [
            '${ps.police.pcore.enums.v1.ContactMethod.CURRENT_ADDRESS.value()}',
            '${ps.police.pcore.enums.v1.ContactMethod.ORIGINAL_ADDRESS.value()}',
            '${ps.police.pcore.enums.v1.ContactMethod.WORK_ADDRESS.value()}',
            '${ps.police.pcore.enums.v1.ContactMethod.OTHER_ADDRESS.value()}'];

        $("#contactMethodId").change(function () {
            var selectedContactMethod = $(this).val();
            var selectedContactMethodText = "";
            if ($('#contactMethodId').val() && $('#contactMethodId').data("select2") && $('#contactMethodId').select2('data').length > 0) {
                selectedContactMethodText = $('#contactMethodId').select2('data')[0].text;
            }
            if ($.inArray(selectedContactMethod, addressContactMethodsArray) > -1) {
                $("#contactInfoLocationWrapperId").show();
                $(".valueFormGroup div").removeClass("isRequired");
                $(".valueFormGroup label").text('${message(code: 'default.details.label')} ' + selectedContactMethodText);
                $("#value").removeClass("isRequired");
            } else {
                $("#contactInfoLocationWrapperId").hide();
                $(".valueFormGroup div").addClass("isRequired");
                $(".valueFormGroup label").text('${message(code: 'default.value.label')}  ' + selectedContactMethodText);
                $("#value").addClass("isRequired");
            }

        });

        function generateFilterParams() {
            var searchParams = {};
            searchParams['ids[]'] = "${contactMethodAutoCompleteFilter}";
            return searchParams;
        }
    </script>