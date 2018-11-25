<el:validatableForm name="personMaritalStatusForm" controller="personMaritalStatus" action="update">
    <el:hiddenField name="id" value="${personMaritalStatus?.id}" />
    <g:render template="/pcore/person/personMaritalStatus/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                      personMaritalStatus:personMaritalStatus]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>