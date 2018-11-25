<el:validatableForm name="personCharacteristicsForm" controller="personCharacteristics" action="update">
    <el:hiddenField name="id" value="${personCharacteristics?.id}" />
    <g:render template="/pcore/person/personCharacteristics/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                      personCharacteristics:personCharacteristics]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>