<script type="text/javascript">
    function callBackContactInfo(json) {
        if (json.success) {
            if (json.data && json.data.department && json.data.department.id) {
                $("#departmentId").val(json.data.department.id);
                var newOption = new Option("${joinedDepartmentOperationalTasks?.department?.descriptionInfo?.localName}", "${joinedDepartmentOperationalTasks?.department?.id}", true, true);
                $('#departmentId').append(newOption);
                $('#departmentId').trigger('change');
            }
            var clickedButton = $('button[formButtonClicked="true"]').first();
            if (clickedButton.attr("closeModal")) {
                renderInLineList();
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackContactInfo"
                         name="joinedDepartmentOperationalTasksForm"
                         controller="joinedDepartmentOperationalTasks"
                         action="save">

    <g:render template="/joinedDepartmentOperationalTasks/form" model="[
            isDepartmentDisabled: isDepartmentDisabled ?: params.isDepartmentDisabled,
            isRelatedObjectTypeDisabled: isRelatedObjectTypeDisabled ?: params.isRelatedObjectTypeDisabled,
            joinedDepartmentOperationalTasks: joinedDepartmentOperationalTasks]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>