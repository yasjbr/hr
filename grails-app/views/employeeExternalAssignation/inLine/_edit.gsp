<el:validatableForm name="employeeExternalAssignationForm" controller="employeeExternalAssignation" action="update">
    <el:hiddenField name="id" value="${employeeExternalAssignation?.id}" />
    <g:render template="/employeeExternalAssignation/form" model="[employeeExternalAssignation:employeeExternalAssignation]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>