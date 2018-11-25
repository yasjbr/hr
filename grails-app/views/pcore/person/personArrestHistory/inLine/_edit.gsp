<script type="text/javascript">
    function resetDetails() {
        gui.formValidatable.removeRequiredField('personArrestHistoryForm', 'arrestJudgementTypeTemp');
        gui.formValidatable.removeRequiredField('personArrestHistoryForm', 'arrestPeriodTemp');
        gui.formValidatable.removeRequiredField('personArrestHistoryForm', 'unitOfMeasurementTemp');
        return gui.formValidatable.validate('personArrestHistoryForm');
    }
</script>
<el:validatableForm callBackBeforeSendFunction="resetDetails" name="personArrestHistoryForm" controller="personArrestHistory" action="update">
    <el:hiddenField name="id" value="${personArrestHistory?.id}" />
    <g:render template="/pcore/person/personArrestHistory/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                           personArrestHistory:personArrestHistory]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>