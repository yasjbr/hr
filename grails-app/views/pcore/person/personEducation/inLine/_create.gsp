<script type="text/javascript">
    function callBackPersonEducation(json) {
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personEducation?.person?.localFullName}", "${personEducation?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');


                var clickedButton = $('button[formButtonClicked="true"]').first();
                if (clickedButton.attr("closeModal")) {
                    renderInLineList();
                }
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackPersonEducation" name="personEducationForm"
                         controller="personEducation" action="save">
    <g:render template="/pcore/person/personEducation/form"
              model="[
                      organizationCallBackId : 'organizationId',
                      personCallBackId       : 'personId',
                      isOrganizationDisabled : isOrganizationDisabled ?: params.isOrganizationDisabled,
                      isPersonDisabled       : isPersonDisabled ?: params.isPersonDisabled,
                      isDocumentOwnerDisabled: isDocumentOwnerDisabled ?: params.isDocumentOwnerDisabled,
                      personEducation        : personEducation]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>