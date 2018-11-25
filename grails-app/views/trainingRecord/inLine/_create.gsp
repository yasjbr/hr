<script type="text/javascript">
    function callBackTrainingRecord(json) {
        if (json.success) {
            if (json.data && json.data.employee && json.data.employee.id) {
                $("#employeeId").val(json.data.employee.id);
                var newOption = new Option("${trainingRecord?.employee?.transientData?.personDTO?.localFullName}", "${trainingRecord?.employee?.id}", true, true);
                $('#employeeId').append(newOption);
                $('#employeeId').trigger('change');


                var clickedButton = $('button[formButtonClicked="true"]').first();
                if (clickedButton.attr("closeModal")) {
                    renderInLineList();
                }
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackTrainingRecord" name="trainingRecordForm"
                         controller="trainingRecord" action="save">
    <g:render template="/trainingRecord/form"
              model="[employeeCallBackId: 'employeeId',
                      isEmployeeDisabled: isEmployeeDisabled ?: params.isEmployeeDisabled,
                      trainingRecord    : trainingRecord,
                      firmId            : firmId]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName, action: 'list')}"
                   onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>