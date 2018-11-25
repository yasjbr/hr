<el:validatableForm name="departmentContactInfoForm" controller="departmentContactInfo" action="update">
    <el:hiddenField name="id" value="${departmentContactInfo?.id}" />
    <el:hiddenField name="department.id" value="${departmentContactInfo?.department.id}"/>
    <g:render template="/departmentContactInfo/form" model="[departmentContactInfo:departmentContactInfo]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>
