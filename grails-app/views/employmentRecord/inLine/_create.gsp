<script type="text/javascript">
    function callBackEmploymentRecord(json) {
        if (json.success) {
            if (json.data && json.data.employee && json.data.employee.id) {
                $("#employeeId").val(json.data.employee.id);
                var newOption = new Option("${employmentRecord?.employee?.transientData?.personDTO?.localFullName}", "${employmentRecord?.employee?.id}", true, true);
                $('#employeeId').append(newOption);
                $('#employeeId').trigger('change');


                var clickedButton = $('button[formButtonClicked="true"]').first();
                if (clickedButton.attr("closeModal")) {
                    reloadEmployeeMainData(json.data,true);
                }
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackEmploymentRecord" name="employmentRecordForm"
                         controller="employmentRecord" action="save">
    <g:render template="/employmentRecord/form"
              model="[
                      employeeCallBackId      : 'employeeId',
                      isEmployeeDisabled      : isEmployeeDisabled ?: params.isEmployeeDisabled,
                      employmentRecord   : employmentRecord]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose" isConfirm="true" />
    <el:formButton isSubmit="true" functionName="saveAndCreate" isConfirm="true" />
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>