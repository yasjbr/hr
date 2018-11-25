%{--<g:render template="/pcore/organization/wrapper"--}%
          %{--model="[name:'parentOrganization.id',id:'parentOrganizationId',messageValue:message(code: 'organization.parentOrganization.label'),bean:organization?.parentOrganization,name:'parentOrganization.id',isRequired:false]" />--}%

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="pcore" action="corporationClassificationAutoComplete" name="corporationClassification.id" id="corporationClassificationId" label="${message(code:'organization.corporationClassification.label',default:'corporationClassification')}" values="${[[organization?.corporationClassification?.id,organization?.corporationClassification?.descriptionInfo?.localName]]}" />
</el:formGroup>


<el:formGroup id="parentDiv">
    <el:textField id="localNameWithParent"  name="descriptionInfo.localName" size="8" class=" isRequired"
                     label="${message(code:'organization.descriptionInfo.localName.label',default:'localName')}"
                     value="${descriptionInfo}"
                     idLabel="parentOrganizationLabelId"
    />
</el:formGroup>


<el:formGroup>
    <el:textField  name="descriptionInfo.latinName" size="8" id="latinNameId"
                   label="${message(code:'descriptionInfo.latinName.label',default:'latinName')}"
                   value="${organization?.descriptionInfo?.latinName}"/>
</el:formGroup>

<el:formGroup>
    <el:textField  name="descriptionInfo.hebrewName" size="8" id="hebrewNameId"
                   label="${message(code:'descriptionInfo.hebrewName.label',default:'hebrewName')}"
                   value="${organization?.descriptionInfo?.hebrewName}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="pcore" action="organizationActivityAutoComplete" id="organizationMainActivityId" name="organizationMainActivity.id" label="${message(code:'organization.organizationMainActivity.label',default:'organizationMainActivity')}" values="${[[organization?.organizationMainActivity?.id,organization?.organizationMainActivity?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="latinDescription" id="latinDescriptionId" size="8"  class=" isRequired" label="${message(code:'organization.latinDescription.label',default:'latinDescription')}" value="${organization?.latinDescription}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="localDescription" id="localDescriptionId" size="8"  class=" isRequired" label="${message(code:'organization.localDescription.label',default:'localDescription')}" value="${organization?.localDescription}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="missionStatement" id="missionStatementId" size="8"  class="" label="${message(code:'organization.missionStatement.label',default:'missionStatement')}" value="${organization?.missionStatement}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="pcore" action="organizationTypeAutoComplete" name="organizationType.id" id="organizationTypeId" label="${message(code:'organization.organizationType.label',default:'organizationType')}" values="${[[organization?.organizationType?.id,organization?.organizationType?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="registrationNumber"  id="registrationNumberId" size="8"  class=" isRequired" label="${message(code:'organization.registrationNumber.label',default:'registrationNumber')}" value="${organization?.registrationNumber}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="taxId" id="taxId" size="8"  class="" label="${message(code:'organization.taxId.label',default:'taxId')}" value="${organization?.taxId}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="pcore" action="workingSectorAutoComplete" id="workingSectorId" name="workingSector.id" label="${message(code:'organization.workingSector.label',default:'workingSector')}" values="${[[organization?.workingSector?.id,organization?.workingSector?.descriptionInfo?.localName]]}" />
</el:formGroup>

<script>
    $("#parentOrganizationId").change(function () {
        var val = $(this).val();
        var text = $("#parentOrganizationId option[value='"+val+"']").text();
        if (text) {
            $("#parentOrganizationLabelId").html(text);
            $('#noParentDiv').hide();
            $('#parentDiv').show();
            $('#localNameWithoutParent').prop('disabled', true);
            $('#localNameWithParent').prop('disabled', false);
        }else{
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


