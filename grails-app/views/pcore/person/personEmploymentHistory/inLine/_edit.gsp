<el:validatableForm name="personEmploymentHistory" controller="personEmploymentHistory" action="update">
    <el:hiddenField name="id" value="${personEmploymentHistory?.id}" />
    <g:render template="/pcore/person/personEmploymentHistory/form" model="[isPersonDisabled       : isPersonDisabled ?: params.isPersonDisabled,
                                                               personEmploymentHistory:personEmploymentHistory]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>