<el:dataTable id="personTrainingHistoryTable"
              searchFormName="personTrainingHistorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personTrainingHistory" spaceBefore="true"
              hasRow="true" action="filter"
              serviceName="personTrainingHistory"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personTrainingHistory', action: 'show')}"
                            functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personTrainingHistory')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personTrainingHistory', action: 'edit')}"
                            functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personTrainingHistory')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personTrainingHistory" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personTrainingHistory')}"/>
        <el:dataTableAction controller="personTrainingHistory" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personTrainingHistory')}"/>
    </g:else>
    <el:dataTableAction controller="personTrainingHistory" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personTrainingHistory')}"/>


    <el:dataTableAction
            functionName="openAttachmentModal"
            accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
            actionParams="['id','personId']"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'attachment.entities')}"/>

</el:dataTable>

<g:render template="/attachment/attachmentSharedTemplate" model="[
        referenceObject:referenceObject ,
        operationType:operationType,
        sharedOperationType:sharedOperationType,
        attachmentTypeList:attachmentTypeList,
        isNonSharedObject:true
]"/>
