<el:validatableForm name="employeeStatusHistoryForm" controller="employeeStatusHistory" action="update">
    <el:hiddenField name="id" value="${employeeStatusHistory?.id}" />
    <g:render template="/employeeStatusHistory/form" model="[isEmployeeDisabled : isEmployeeDisabled ?: params.isEmployeeDisabled,
                                                   employeeStatusHistory:employeeStatusHistory]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>