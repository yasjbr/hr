<el:dataTable id="loanNoticeReplayRequestTable" 
              searchFormName="loanNoticeReplayRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" 
              widthClass="col-sm-12" 
              controller="loanNoticeReplayRequest" 
              spaceBefore="true"
              hasRow="true" 
              action="filter" 
              serviceName="loanNoticeReplayRequest" 
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'loanNoticeReplayRequest', action: 'show')}"
                            actionParams="encodedId"  functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show loanNoticeReplayRequest')}"/>

        <el:dataTableAction accessUrl="${createLink(controller: 'loanNoticeReplayRequest', action: 'edit')}"
                            actionParams="encodedId" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            showFunction="viewEditAction" message="${message(code: 'default.edit.label', args: [entity],
                                    default: 'show loanNoticeReplayRequest')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="loanNoticeReplayRequest" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show loanNoticeReplayRequest')}"/>

        <el:dataTableAction controller="loanNoticeReplayRequest" action="edit" actionParams="encodedId"
                            showFunction="manageExecuteEdit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit loanList')}"/>

    </g:else>

    <el:dataTableAction showFunction="manageExecuteDelete" controller="loanNoticeReplayRequest" action="delete"
                        actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity],
                                default: 'delete loanNoticeReplayRequest')}"/>

    <g:if test="${hasAttachment}">
        <el:dataTableAction
                functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
                actionParams="id"
                class="blue icon-attach"
                type="function"
                message="${message(code:'attachment.entities')}"/>
    </g:if>


    <el:dataTableAction controller="loanNoticeReplayRequest" action="goToList" showFunction="manageListLink2"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'loanNoticeReplayList.entities', default: 'loanNoticeReplayList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>

</el:dataTable>
<script>
    function manageListLink2(row) {
        if (row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}") {
            return true;
        }
        return false;
    }
</script>
