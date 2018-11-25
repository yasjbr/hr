<script type="text/javascript">
    function callBackEmployeeInternalAssignation(json) {
        if (json.success) {
            var clickedButton = $('button[formButtonClicked="true"]').first();
            if (clickedButton.attr("closeModal")) {
                renderInLineList();
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackEmployeeInternalAssignation" name="employeeInternalAssignationForm"
                         controller="employeeInternalAssignation" action="save">
    <g:render template="/employeeInternalAssignation/form"
              model="[employeeInternalAssignation   : employeeInternalAssignation]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>