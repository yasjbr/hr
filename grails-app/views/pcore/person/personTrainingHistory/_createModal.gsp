<script type="text/javascript">
    function callBackPersonTrainingHistory(json){
        if (json.success) {
            if (json.data && json.data.trainee && json.data.trainee.id) {
                $("#traineeId").val(json.data.trainee.id);
                var newOption = new Option("${personTrainingHistory?.trainee?.localFullName}", "${personTrainingHistory?.trainee?.id}", true, true);
                $('#traineeId').append(newOption);
                $('#traineeId').trigger('change');
            }


            _dataTables['personTrainingHistoryTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonTrainingHistory" title="${message(code: 'default.create.label',args: [message(code:'personTrainingHistory.entity')])}"
                              width="70%" name="personTrainingHistoryForm" controller="personTrainingHistory" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personTrainingHistory/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'traineeId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personTrainingHistory:personTrainingHistory]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
