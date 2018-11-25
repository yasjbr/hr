<script type="text/javascript">
    function callBackContactInfo(json) {
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${contactInfo?.person?.localFullName}", "${contactInfo?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');

            } else if (json.data && json.data.organization && json.data.organization.id) {
                $("#organizationId").val(json.data.organization.id);
                var newOption = new Option("${contactInfo?.organization?.descriptionInfo?.localName}", "${contactInfo?.organization?.id}", true, true);
                $('#organizationId').append(newOption);
                $('#organizationId').trigger('change');
            }
            var clickedButton = $('button[formButtonClicked="true"]').first();
            if (clickedButton.attr("closeModal")) {
                renderInLineList();
            }
        }
    }

</script>
<el:validatableResetForm callBackFunction="callBackContactInfo"
                         name="contactInfoForm"
                         controller="contactInfo"
                         action="save">

    <g:render template="/pcore/person/contactInfo/form" model="[
            organizationCallBackId     : 'organizationId',
            personCallBackId           : 'personId',
            isOrganizationDisabled     : isOrganizationDisabled ?: params.isOrganizationDisabled,
            isPersonDisabled           : isPersonDisabled ?: params.isPersonDisabled,
            isRelatedObjectTypeDisabled: isRelatedObjectTypeDisabled ?: params.isRelatedObjectTypeDisabled,
            contactInfo                : contactInfo]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>