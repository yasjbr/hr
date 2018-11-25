<el:dataTable id="personHealthHistoryTable"
              searchFormName="personHealthHistorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personHealthHistory"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="personHealthHistory"
              domainColumns="${domainColumns?:"DOMAIN_COLUMNS"}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personHealthHistory',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personHealthHistory')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personHealthHistory',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personHealthHistory')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personHealthHistory" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personHealthHistory')}"/>
        <el:dataTableAction controller="personHealthHistory" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personHealthHistory')}"/>
    </g:else>
    <el:dataTableAction controller="personHealthHistory" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personHealthHistory')}"/>


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