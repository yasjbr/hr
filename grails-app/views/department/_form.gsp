<div style="padding-right: 40px;,padding-bottom: 15px;">
    <h4 class=" smaller lighter blue">
        ${message(code: 'department.info.label')}</h4> <hr/></div>


<g:render template="/DescriptionInfo/wrapper" model="[bean: department?.descriptionInfo]"/>




<sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_AOC.value}">
    <el:formGroup>
        <el:autocomplete
                optionKey="id"
                optionValue="name"
                size="8"
                class=" isRequired"
                controller="firm"
                action="autocomplete"
                name="firm.id"
                label="${message(code: 'firm.label', default: 'firm')}"
                values="${[[department?.firm?.id, department?.firm?.name]]}"/>
    </el:formGroup>
</sec:ifAnyGranted>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="isRequired" controller="departmentType"
                     action="autocomplete" name="departmentType.id"
                     label="${message(code: 'department.departmentType.label', default: 'departmentType')}"
                     values="${[[department?.departmentType?.id, department?.departmentType?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="department" action="autocomplete"
                     name="functionalParentDeptId"
                     label="${message(code: 'department.functionalParentDeptId.label', default: 'functionalParentDept')}"
                     values="${[[department?.functionalParentDeptId, department?.transientData?.functionalParentDeptName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="department" action="autocomplete"
                     name="managerialParentDeptId"
                     label="${message(code: 'department.managerialParentDeptId.label', default: 'managerialParentDept')}"
                     values="${[[department?.managerialParentDeptId, department?.transientData?.managerialParentDeptName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:textArea name="note" size="8" label="${message(code: 'department.note.label', default: 'note')}"
                 value="${department?.note}"/>
</el:formGroup>

%{-------------------------------------------------------------------------------------}%

<div style="padding-right: 40px;,padding-bottom: 15px;">
    <h4 class=" smaller lighter blue">
        ${message(code: 'department.location.label')}</h4> <hr/></div>

<el:hiddenField name="edit_locationId" value="${department?.locationId}"/>
<br/>
<g:render template="/pcore/location/staticWrapper"
          model="[location          : department?.transientData?.locationDTO,
                  isRequired        : true,
                  size              : 8,
                  isRegionRequired  : false,
                  isCountryRequired : false,
                  isDistrictRequired: false]"/>
<el:formGroup>
    <el:textArea name="unstructuredLocation" size="8" class=""
                 label="${message(code: 'department.unstructuredLocation.label', default: 'unstructuredLocation')}"
                 value="${department?.unstructuredLocation}"/>
</el:formGroup>



<script>
    $("#parentOrganizationId").change(function () {
        var val = $(this).val();
        var text = $("#parentOrganizationId option[value='" + val + "']").text();
        if (text) {
            $("#parentOrganizationLabelId").html(text);
            $('#noParentDiv').hide();
            $('#parentDiv').show();
            $('#localNameWithoutParent').prop('disabled', true);
            $('#localNameWithParent').prop('disabled', false);
        } else {
            $("#parentOrganizationLabelId").html(" ");
            $('#noParentDiv').show();
            $('#parentDiv').hide();
            $('#localNameWithoutParent').prop('disabled', false);
            $('#localNameWithParent').prop('disabled', true);
        }
    });
    $(document).ready(function () {
        $("#parentOrganizationId").trigger("change");
    });
</script>
