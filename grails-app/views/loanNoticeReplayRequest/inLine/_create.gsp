<script type="text/javascript">
    function callBackLoanNominatedEmployee(json) {
        if (json.success) {
            if (json.data && json.data.id) {
                var clickedButton = $('button[formButtonClicked="true"]').first();
                if (clickedButton.attr("closeModal")) {
                    renderInLineList();
                }
            }
        }
    }
</script>
<el:validatableResetForm callBackFunction="callBackLoanNominatedEmployee" name="loanNoticeReplayRequestForm"
                         controller="loanNoticeReplayRequest" action="save">
    <g:render template="/loanNoticeReplayRequest/form"
              model="[loanNoticeReplayRequest: loanNoticeReplayRequest, workflowPathHeader: workflowPathHeader]"/>
    <el:formButton isSubmit="true" withClose="true" functionName="saveAndClose"/>
    <el:formButton isSubmit="true" functionName="saveAndCreate"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName, action: 'list')}"
                   onClick="renderInLineList()"/>
</el:validatableResetForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>