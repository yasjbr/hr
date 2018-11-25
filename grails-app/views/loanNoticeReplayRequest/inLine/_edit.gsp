<el:validatableForm name="loanNoticeReplayRequestForm" controller="loanNoticeReplayRequest" action="update">
    <el:hiddenField name="id" value="${loanNoticeReplayRequest?.id}" />
    <g:render template="/loanNoticeReplayRequest/form" model="[loanNoticeReplayRequest:loanNoticeReplayRequest]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>