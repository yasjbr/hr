<el:validatableForm name="bordersSecurityCoordinationForm" controller="bordersSecurityCoordination" action="update">
    <g:render template="/bordersSecurityCoordination/form" model="[bordersSecurityCoordination]"/>
    <el:formButton isSubmit="true" functionName="saveAndClose"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>

<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>

