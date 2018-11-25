<el:validatableForm name="personRelationShipsForm" controller="personRelationShips" action="update">
    <el:hiddenField name="id" value="${personRelationShips?.id}" />
    <g:render template="/pcore/person/personRelationShips/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                           personRelationShips:personRelationShips]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>