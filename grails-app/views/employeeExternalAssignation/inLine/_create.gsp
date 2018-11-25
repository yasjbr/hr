<script type="text/javascript">
    function callBackEmployeeExternalAssignation(json) {
        if (json.success) {
            var clickedButton = $('button[formButtonClicked="true"]').first();
            if (clickedButton.attr("closeModal")) {
                renderInLineList();
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackEmployeeExternalAssignation" name="employeeExternalAssignationForm"
                         controller="employeeExternalAssignation" action="save">
    <g:render template="/employeeExternalAssignation/form"
              model="[employeeExternalAssignation   : employeeExternalAssignation]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>