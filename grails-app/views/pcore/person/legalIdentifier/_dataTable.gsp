<el:dataTable id="legalIdentifierTable" searchFormName="legalIdentifierSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="legalIdentifier" spaceBefore="true"
              hasRow="true" action="filter"
              serviceName="legalIdentifier"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'legalIdentifier', action: 'show')}"
                            functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show legalIdentifier')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'legalIdentifier', action: 'edit')}"
                            functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit legalIdentifier')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="legalIdentifier" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show legalIdentifier')}"/>
        <el:dataTableAction controller="legalIdentifier" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit legalIdentifier')}"/>
    </g:else>
    <el:dataTableAction controller="legalIdentifier" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete legalIdentifier')}"/>



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