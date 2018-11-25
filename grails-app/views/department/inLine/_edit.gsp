<el:validatableForm name="joinedDepartmentOperationalTasksForm" controller="department" action="updateDepartmentOperationalTask">
    <el:hiddenField name="id" value="${recruitmentCycle?.id}" />

    <g:render template="/department/operationalTask/manageOperationalTask" model="[department]"/>

    <el:formButton isSubmit="true" functionName="saveAndClose"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>

<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>

