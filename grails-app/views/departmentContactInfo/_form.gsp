<lay:wall title="">
    <g:render template="/department/wrapper" model="[id        : (departmentCallBackId ?: 'departmentId'),
                                                     name      : 'departmentId.id',
                                                     bean      : departmentContactInfo?.department,
                                                     isRequired: false,
                                                     isDisabled: isDepartmentDisabled]"/>
    <el:hiddenField name="department.id" value="${params?.department?.id}"/>

</lay:wall>



<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="contactTypeAutoComplete"
                     name="contactTypeId"
                     label="${message(code: 'departmentContactInfo.contactType.label', default: 'Contact Type')}"
                     values="${[[departmentContactInfo?.contactTypeId, departmentContactInfo?.transientData?.contactTypeName]]}"
                     id="contactTypeId"/>
</el:formGroup>


<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="contactMethodAutoComplete" paramsGenerateFunction="phoneParams"
                     name="contactMethodId"
                     label="${message(code: 'departmentContactInfo.contactMethod.label', default: 'Contact Method')}"
                     values="${[[departmentContactInfo?.contactMethodId, departmentContactInfo?.transientData?.contactMethodName]]}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="value" size="8" class=" isRequired"
                  label="${message(code: 'departmentContactInfo.value.label', default: 'value')}"
                  value="${departmentContactInfo?.value}"/>
</el:formGroup>





<script>
    $("#contactMethodId").change(function () {
        var selectedContactMethod = $(this).val();
        var selectedContactMethodText = "";
        if ($('#contactMethodId').val() && $('#contactMethodId').data("select2") && $('#contactMethodId').select2('data').length > 0) {
            selectedContactMethodText = $('#contactMethodId').select2('data')[0].text;
        }
    });
    $(document).ready(function () {
        $("#relatedObjectType").trigger("change");
        $("#contactMethodId").trigger("change");
    });


    function phoneParams() {
        var searchParams = {};
        searchParams['ids[]'] = "${[ps.police.pcore.enums.v1.ContactMethodEnum.PHONE_NUMBER.value(), ps.police.pcore.enums.v1.ContactMethodEnum.MOBILE_NUMBER.value(),ps.police.pcore.enums.v1.ContactMethodEnum.FAX.value()]}";
        return searchParams;
    }
</script>