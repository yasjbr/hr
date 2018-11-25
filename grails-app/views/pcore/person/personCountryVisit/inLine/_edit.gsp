<el:validatableForm name="personCountryVisitForm" controller="personCountryVisit" action="update">
    <el:hiddenField name="id" value="${personCountryVisit?.id}" />
    <g:render template="/pcore/person/personCountryVisit/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                      personCountryVisit:personCountryVisit]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>