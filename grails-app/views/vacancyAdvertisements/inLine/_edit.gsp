<el:validatableForm name="vacancyForm" controller="vacancyAdvertisements" action="updateVacancy">
    <g:render template="/vacancy/form" model="[vacancyAdvertisements]"/>
    <el:formButton isSubmit="true" functionName="saveAndClose"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>

<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>

