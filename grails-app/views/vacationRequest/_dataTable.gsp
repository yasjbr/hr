<el:dataTable id="vacationRequestTable" searchFormName="vacationRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="vacationRequest" spaceBefore="true" hasRow="true"
              action="filter" serviceName="vacationRequest" domainColumns="${domainColumns}" >

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'vacationRequest', action: 'showThread')}"
                            actionParams="threadId" functionName="renderInLineShowThread" type="function" class="green icon-list"
                            message="${message(code: 'request.showThread.label', default: 'show vacationRequest Thread')}"/>
    </g:if>
    <g:else>

        <el:dataTableAction controller="vacationRequest" action="show" actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show vacationRequest')}"/>

        <el:dataTableAction controller="vacationRequest" action="edit" actionParams="encodedId" class="blue icon-pencil"
                            showFunction="manageExecuteEdit"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit vacationRequest')}"/>

        <el:dataTableAction controller="vacationRequest" action="delete" actionParams="encodedId" class="red icon-trash"
                            type="confirm-delete" showFunction="manageExecuteDelete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete vacationRequest')}"/>



        <el:dataTableAction controller="request" action="editRequestCreate" showFunction="manageEditRequest" actionParams="encodedId"
                            class="blue icon-pencil" type="modal-ajax" preventCloseOutSide="true"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit vacationRequest')}"/>
        <el:dataTableAction
                preventCloseOutSide="true"
                actionParams="encodedId" controller="request" action="extendRequestCreate"
                class="green icon-list-add" type="modal-ajax" showFunction="manageExtendRequest"
                message="${message(code: 'vacationRequest.continue.vacation.label', args: [entity], default: 'vacationContinueRequest')}"/>

        <el:dataTableAction
                preventCloseOutSide="true"
                actionParams="encodedId" controller="request" action="stopRequestCreate"
                class="red icon-stop-3" type="modal-ajax" showFunction="manageStopRequest"
                message="${message(code: 'vacationRequest.stop.vacation.label', args: [entity], default: 'vacationStopRequest')}"/>

        <el:dataTableAction preventCloseOutSide="true" actionParams="encodedId" controller="request" action="cancelRequestCreate"
                            class="blue icon-stop-3" type="modal-ajax" showFunction="manageCancelRequest"
                            message="${message(code: 'vacationRequest.cancel.vacation.label', args: [entity], default: 'vacationCancelRequest')}"/>

        <el:dataTableAction
                functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
                actionParams="id"
                class="blue icon-attach"
                type="function"
                message="${message(code: 'default.attachment.label')}"/>

        <el:dataTableAction controller="vacationRequest" action="goToList" showFunction="manageListLink"
                            actionParams="encodedId" class="icon-th-list-5"
                            message="${message(code: 'vacationList.label', default: 'vacationList')}"/>

        <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                            showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                            functionName="viewOrderInfoModal" type="function"
                            message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
    </g:else>

</el:dataTable>