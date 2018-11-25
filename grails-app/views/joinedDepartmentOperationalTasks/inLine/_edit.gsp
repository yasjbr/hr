<el:validatableForm name="joinedDepartmentOperationalTasks" controller="joinedDepartmentOperationalTasks" action="update">
    <el:hiddenField name="id" value="${departmentContactInfo?.id}" />
    <g:render template="/joinedDepartmentOperationalTasks/form" model="[joinedDepartmentOperationalTasks:joinedDepartmentOperationalTasks]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>