<el:dataTable id="trainingRecordTable" searchFormName="trainingRecordSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="trainingRecord"
              spaceBefore="true" hasRow="true" action="filter" serviceName="trainingRecord"
              domainColumns="${domainColumns}">



    <g:if test="${isInLineActions}">

        <el:dataTableAction accessUrl="${createLink(controller: 'trainingRecord',
                action: 'show')}" functionName="renderInLineShow" type="function"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show trainingRecord')}" />


        <el:dataTableAction accessUrl="${createLink(controller: 'trainingRecord',
                action: 'edit')}" functionName="renderInLineEdit" type="function"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit trainingRecord')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="trainingRecord" action="show"
                            actionParams="encodedId" class="green icon-eye" 
                            message="${message(code:'default.show.label',args:[entity],default:'show trainingRecord')}" />
        
        <el:dataTableAction controller="trainingRecord" action="edit" 
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code:'default.edit.label',args:[entity],default:'edit trainingRecord')}" />


    </g:else>
    

    <el:dataTableAction controller="trainingRecord"
                        action="delete" actionParams="encodedId"
                        class="red icon-trash" type="confirm-delete"
                        message="${message(code:'default.delete.label',args:[entity],default:'delete trainingRecord')}" />

    <el:dataTableAction
            functionName="openAttachmentModal"
            accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="['id','employeeId']"
            class="blue icon-attach"
            type="function"
            message="${message(code:'attachment.entities')}"/>

</el:dataTable>





<g:render template="/attachment/attachmentSharedTemplate" model="[
        referenceObject:referenceObject ,
        operationType:operationType,
        sharedOperationType:sharedOperationType,
        attachmentTypeList:attachmentTypeList,
        isNonSharedObject:true
]"/>
