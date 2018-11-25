<el:validatableForm name="personHealthHistoryForm" controller="personHealthHistory" action="update">
    <el:hiddenField name="id" value="${personHealthHistory?.id}" />
    <g:render template="/pcore/person/personHealthHistory/form" model="[isPersonDisabled : isPersonDisabled ?: params.isPersonDisabled,
                                                           personHealthHistory:personHealthHistory]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>