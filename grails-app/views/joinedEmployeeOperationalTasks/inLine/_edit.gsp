<el:validatableForm name="joinedEmployeeOperationalTasksForm" controller="joinedEmployeeOperationalTasks" action="update">
    <el:hiddenField name="id" value="${joinedEmployeeOperationalTasks?.id}" />
    <g:render template="/joinedEmployeeOperationalTasks/form" model="[isEmployeeDisabled : isEmployeeDisabled ?: params.isEmployeeDisabled,
                                                           joinedEmployeeOperationalTasks:joinedEmployeeOperationalTasks]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>