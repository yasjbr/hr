<script type="text/javascript">
    function callBackContactInfo(json) {
        if (json.success) {
            if (json.data && json.data.firm && json.data.firm.id) {
                $("#firmId").val(json.data.firm.id);
                var newOption = new Option("${firmSupportContactInfo?.firm?.name}", "${firmSupportContactInfo?.firm?.id}", true, true);
                $('#firmId').append(newOption);
                $('#firmId').trigger('change');
            }
            var clickedButton = $('button[formButtonClicked="true"]').first();
            if (clickedButton.attr("closeModal")) {
                renderInLineList();
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackContactInfo"
                         name="firmSupportContactInfoForm"
                         controller="firmSupportContactInfo"
                         action="save">

    <g:render template="/firmSupportContactInfo/form" model="[
            isfirmDisabled: isfirmDisabled ?: params.isfirmDisabled,
            isRelatedObjectTypeDisabled: isRelatedObjectTypeDisabled ?: params.isRelatedObjectTypeDisabled,
            firmSupportContactInfo: firmSupportContactInfo]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>