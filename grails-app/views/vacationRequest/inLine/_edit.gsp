<el:validatableForm name="vacationRequestForm" controller="vacationRequest" action="update">
    <g:render template="/vacationRequest/form" model="[vacationRequest]"/>
    <el:formButton isSubmit="true" functionName="saveAndClose"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>

<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>

