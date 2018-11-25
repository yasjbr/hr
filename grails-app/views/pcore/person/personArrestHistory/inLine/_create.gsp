<script type="text/javascript">
    function callBackPersonArrestHistory(json) {
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personArrestHistory?.person?.localFullName}", "${personArrestHistory?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');

                var clickedButton = $('button[formButtonClicked="true"]').first();
                if (clickedButton.attr("closeModal")) {
                    renderInLineList();
                }
            }
        }
    }


    function resetDetails() {
        gui.formValidatable.removeRequiredField('personArrestHistoryForm', 'arrestJudgementTypeTemp');
        gui.formValidatable.removeRequiredField('personArrestHistoryForm', 'arrestPeriodTemp');
        gui.formValidatable.removeRequiredField('personArrestHistoryForm', 'unitOfMeasurementTemp');
        return gui.formValidatable.validate('personArrestHistoryForm');
    }

</script>
<el:validatableResetForm  callBackBeforeSendFunction="resetDetails" callBackFunction="callBackPersonArrestHistory"
                         name="personArrestHistoryForm"
                         controller="personArrestHistory"
                         action="save">
    <g:render template="/pcore/person/personArrestHistory/form" model="[
            organizationCallBackId: 'organizationId',
            personCallBackId      : 'personId',
            isOrganizationDisabled: isOrganizationDisabled ?: params.isOrganizationDisabled,
            isPersonDisabled      : isPersonDisabled ?: params.isPersonDisabled,
            personArrestHistory   : personArrestHistory]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>