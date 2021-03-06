<script type="text/javascript">
    function callBackProfileNote(json) {
        if (json.success) {
            if (json.data && json.data.employee && json.data.employee.id) {
                $("#employeeId").val(json.data.employee.id);
                var newOption = new Option("${employeeStatusHistory?.employee?.transientData?.personDTO?.localFullName}", "${employeeStatusHistory?.employee?.id}", true, true);
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
<el:validatableResetForm callBackFunction="callBackProfileNote" name="employeeStatusHistoryForm"
                         controller="employeeStatusHistory" action="save">
    <g:render template="/employeeStatusHistory/form"
              model="[
                      employeeCallBackId      : 'employeeId',
                      isEmployeeDisabled      : isEmployeeDisabled ?: params.isEmployeeDisabled,
                      employeeStatusHistory   : employeeStatusHistory]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>