<el:validatableForm name="employeeInternalAssignationForm" controller="employeeInternalAssignation" action="update">
    <el:hiddenField name="id" value="${employeeInternalAssignation?.id}" />
    <g:render template="/employeeInternalAssignation/form" model="[employeeInternalAssignation:employeeInternalAssignation]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>