<el:dataTable id="disciplinaryRequestTable" searchFormName="disciplinaryRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="disciplinaryRequest" spaceBefore="true" hasRow="true"
              action="filter" serviceName="disciplinaryRequest" domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'disciplinaryRequest', action: 'show')}"
                            actionParams="encodedId" functionName="renderInLineShow" type="function"
                            class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show disciplinaryRequest')}"/>

        <el:dataTableAction controller="disciplinaryRequest" action="showDetails"
                            type="modal-ajax" actionParams="encodedId" class="blue icon-list"
                            message="${message(code:'disciplinaryRequest.viewDetails.label',default:'view details')}" />


        <el:dataTableAction controller="petitionRequest" action="showRelatedRequest" showFunction="viewReturnAction"
                            type="modal-ajax" actionParams="disciplinaryRequestId" class="grey icon-reply"
                            message="${message(code:'petitionRequest.show.label',default:'view petitionRequest details')}" />

    </g:if>
    <g:else>

        <el:dataTableAction controller="disciplinaryRequest" action="show" actionParams="encodedId"
                            class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show disciplinaryRequest')}"/>


        <el:dataTableAction controller="disciplinaryRequest" action="edit" showFunction="manageExecuteEdit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit disciplinaryRequest')}"/>


        <el:dataTableAction controller="disciplinaryRequest" action="delete" showFunction="manageExecuteDelete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete disciplinaryRequest')}"/>

        <el:dataTableAction controller="disciplinaryRequest" action="showDetails"
                            type="modal-ajax" actionParams="encodedId" class="blue icon-list"
                            message="${message(code: 'disciplinaryRequest.viewDetails.label', default: 'view details')}"/>


        <el:dataTableAction controller="petitionRequest" action="createNewRequest" showFunction="createPetitionAction"
                            actionParams="disciplinaryRequestId" class="grey icon-doc-add"
                            message="${message(code: 'petitionRequest.create.label', default: 'petitionRequest')}"/>

        <el:dataTableAction controller="petitionRequest" action="showRelatedRequest" showFunction="viewPetitionAction"
                            type="modal-ajax" actionParams="disciplinaryRequestId" class="grey icon-doc-text-2"
                            message="${message(code:'petitionRequest.show.label',default:'view petitionRequest details')}" />

        <el:dataTableAction
                functionName="openAttachmentModal"
                accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
                actionParams="id"
                class="blue icon-attach"
                type="function"
                message="${message(code: 'attachment.entities')}"/>

        <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                            showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                            functionName="viewOrderInfoModal" type="function"
                            message="${message(code: 'request.setManagerialOrderInfo.label')}"/>

        <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setExternalManagerialOrder" )}"
                            showFunction="showSetExternalOrderInfo" actionParams="['id','encodedId']" class="red ace-icon icon-lock"
                            functionName="viewExternalOrderInfoModal" type="function"
                            message="${message(code: 'disciplinaryRequest.setExternalManagerialOrder.label')}"/>

    </g:else>
</el:dataTable>