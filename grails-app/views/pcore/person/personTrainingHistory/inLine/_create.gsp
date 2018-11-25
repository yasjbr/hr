<script type="text/javascript">
    function callBackPersonTrainingHistory(json) {
        if (json.success) {
            if (json.data && json.data.trainee && json.data.trainee.id) {
                $("#traineeId").val(json.data.trainee.id);
                var newOption = new Option("${personTrainingHistory?.trainee?.localFullName}", "${personTrainingHistory?.trainee?.id}", true, true);
                $('#traineeId').append(newOption);
                $('#traineeId').trigger('change');


                var clickedButton = $('button[formButtonClicked="true"]').first();
                if (clickedButton.attr("closeModal")) {
                    renderInLineList();
                }
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackPersonTrainingHistory"
                         name="personTrainingHistoryForm" controller="personTrainingHistory" action="save">
    <g:render template="/pcore/person/personTrainingHistory/form"
              model="[
                      organizationCallBackId:'organizationId',
                      personCallBackId:'traineeId',
                      isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                      isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                      personTrainingHistory:personTrainingHistory]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>