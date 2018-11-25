<g:render template="/DescriptionInfo/wrapper" model="[bean: employeeStatus?.descriptionInfo]"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeeStatusCategory"
                     action="autocomplete" name="employeeStatusCategory.id"
                     id="employeeStatusCategoryId"
                     label="${message(code: 'employeeStatus.employeeStatusCategory.label', default: 'employeeStatusCategory')}"
                     values="${[[employeeStatus?.employeeStatusCategory?.id, employeeStatus?.employeeStatusCategory?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<div id="allowReturnToServiceDiv" style="display: none;">
    <el:formGroup>
        <el:checkboxField
                label="${message(code: 'employeeStatus.allowReturnToService.label', default: 'allowReturnToService')}"
                size="8"
                name="allowReturnToService"
                value="${employeeStatus?.allowReturnToService}"
                isChecked="${employeeStatus?.allowReturnToService}"/>
    </el:formGroup>
</div>

<el:formGroup>
    <el:textArea name="description" size="8" class=""
                 label="${message(code: 'employeeStatus.description.label', default: 'description')}"
                 value="${employeeStatus?.description}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'employeeStatus.universalCode.label', default: 'universalCode')}"
                  value="${employeeStatus?.universalCode}"/>
</el:formGroup>

<script>
    $("#employeeStatusCategoryId").on("select2:close", function (e) {
        var value = $('#employeeStatusCategoryId').val();
        if (value == '${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.UNCOMMITTED}') {
            $('#allowReturnToServiceDiv').show();
        } else {
            $('#allowReturnToServiceDiv').hide();
        }
    });

    <g:if test="${employeeStatus?.employeeStatusCategory?.id == ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.UNCOMMITTED.getValue()}">
    $('#allowReturnToServiceDiv').show();
    </g:if>
</script>