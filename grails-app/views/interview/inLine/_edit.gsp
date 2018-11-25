<el:validatableForm name="interviewForm" controller="interview" action="update">
    <g:render template="/interview/form" model="[interview]"/>
    <el:formButton isSubmit="true" functionName="saveAndClose"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>

<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>

