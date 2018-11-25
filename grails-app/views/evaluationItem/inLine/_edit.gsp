<el:validatableForm name="evaluationItemForm" controller="evaluationItem" action="update">
    <el:hiddenField name="encodedId" value="${evaluationItem?.encodedId}" />
    <g:render template="/evaluationItem/form" model="[evaluationItem]"/>
    <el:formButton isSubmit="true" functionName="saveAndClose"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>

<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>

