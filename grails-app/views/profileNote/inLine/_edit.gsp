<el:validatableForm name="profileNoteForm" controller="profileNote" action="update">
    <el:hiddenField name="id" value="${profileNote?.id}" />
    <g:render template="/profileNote/form" model="[isEmployeeDisabled : isEmployeeDisabled ?: params.isEmployeeDisabled,
                                                           profileNote:profileNote]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>