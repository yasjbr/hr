<script type="text/javascript">
    function callBackLegalIdentifier(json) {
        if (json.success) {
            if (json.data && json.data.ownerPerson && json.data.ownerPerson.id) {
                $("#ownerPersonId").val(json.data.ownerPerson.id);
                var newOption = new Option("${legalIdentifier?.ownerPerson?.localFullName}", "${legalIdentifier?.ownerPerson?.id}", true, true);
                $('#ownerPersonId').append(newOption);
                $('#ownerPersonId').trigger('change');
            } else if (json.data && json.data.ownerOrganization && json.data.ownerOrganization.id) {
                $("#ownerOrganizationId").val(json.data.ownerOrganization.id);
                var newOption = new Option("${legalIdentifier?.ownerOrganization?.descriptionInfo?.localName}", "${legalIdentifier?.ownerOrganization?.id}", true, true);
                $('#ownerOrganizationId').append(newOption);
                $('#ownerOrganizationId').trigger('change');
            }

            var clickedButton = $('button[formButtonClicked="true"]').first();
            if (clickedButton.attr("closeModal")) {
                renderInLineList();
            }
        }
    }

</script>
<el:validatableResetForm callBackFunction="callBackLegalIdentifier"
                         name="legalIdentifierForm"
                         controller="legalIdentifier"
                         action="save">
    <g:render template="/pcore/person/legalIdentifier/form"
              model="[
                      organizationCallBackId : 'ownerOrganizationId',
                      personCallBackId       : 'ownerPersonId',
                      isOrganizationDisabled : isOrganizationDisabled ?: params.isOrganizationDisabled,
                      isPersonDisabled       : isPersonDisabled ?: params.isPersonDisabled,
                      isDocumentOwnerDisabled: isDocumentOwnerDisabled ?: params.isDocumentOwnerDisabled,
                      legalIdentifier        : legalIdentifier]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>