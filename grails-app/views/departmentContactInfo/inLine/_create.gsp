<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            if (json.data && json.data.id) {
                var clickedButton = $('button[formButtonClicked="true"]').first();
                if (clickedButton.attr("closeModal")) {
                    renderInLineList();
                }
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackFunction"
                         name="departmentContactInfoForm"
                         controller="departmentContactInfo"
                         action="save">

    <g:render template="/departmentContactInfo/form" model="[
            isDepartmentDisabled       : isDepartmentDisabled ?: params.isDepartmentDisabled,
            isRelatedObjectTypeDisabled: isRelatedObjectTypeDisabled ?: params.isRelatedObjectTypeDisabled,
            departmentContactInfo      : departmentContactInfo]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName, action: 'list')}"
                   onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>