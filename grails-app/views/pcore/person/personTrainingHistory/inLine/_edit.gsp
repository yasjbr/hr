<el:validatableForm name="personTrainingHistoryForm" controller="personTrainingHistory" action="update">
    <el:hiddenField name="id" value="${personTrainingHistory?.id}" />
    <g:render template="/pcore/person/personTrainingHistory/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                             personTrainingHistory:personTrainingHistory]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>