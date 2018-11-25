<g:render template="/loanNoticeReplayRequest/show"
          model="[loanNoticeReplayRequest:loanNoticeReplayRequest]"/>
<el:row />
<div class="clearfix form-actions text-center">
    <g:if test="${loanNoticeReplayRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton accessUrl="${createLink(controller: tabEntityName,action: 'edit')}"  onClick="renderInLineEdit('${loanNoticeReplayRequest?.id}')"/>
    </g:if>
    <btn:backButton accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</div>