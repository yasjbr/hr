<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField class=""
                  name="descriptionInfo.localName" size="${size ?: "8"}"
                  label="${messageValue ?: (message(code: 'descriptionInfo.localName.label', default: 'localName'))}"
                  value="${bean?.localName}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employeeStatusCategory"
                     action="autocomplete" name="employeeStatusCategory.id"
                     id="employeeStatusCategoryId"
                     label="${message(code: 'employeeStatus.employeeStatusCategory.descriptionInfo.localName.label', default: 'employeeStatusCategory')}"/>
</el:formGroup>

<div id="allowReturnToServiceDiv" style="display: none;">
    <el:formGroup>
        <el:checkboxField
                label="${message(code: 'employeeStatus.allowReturnToService.label', default: 'allowReturnToService')}"
                size="8"
                name="allowReturnToService"
                value=""/>
    </el:formGroup>
</div>

<script>
    $("#employeeStatusCategoryId").on("select2:close", function (e) {
        var value = $('#employeeStatusCategoryId').val();
        if (value == '${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.UNCOMMITTED}') {
            $('#allowReturnToServiceDiv').show();
        } else {
            $('#allowReturnToServiceDiv').hide();
        }
    });
</script>