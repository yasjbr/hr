<el:validatableForm name="trainingRecordForm" controller="trainingRecord" action="update">
    <el:hiddenField name="id" value="${trainingRecord?.id}" />
    <g:render template="/trainingRecord/form" model="[isEmployeeDisabled : isEmployeeDisabled ?: params.isEmployeeDisabled,
                                                           trainingRecord:trainingRecord]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>