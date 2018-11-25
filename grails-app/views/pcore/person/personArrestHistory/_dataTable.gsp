
<el:dataTable id="personArrestHistoryTable"
              searchFormName="personArrestHistorySearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personArrestHistory" spaceBefore="true"
              hasRow="true" action="filter"
              serviceName="personArrestHistory"
              domainColumns="${domainColumns}">



    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personArrestHistory',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personArrestHistory')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personArrestHistory',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personArrestHistory')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personArrestHistory" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personArrestHistory')}"/>
        <el:dataTableAction controller="personArrestHistory" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personArrestHistory')}"/>
    </g:else>
    <el:dataTableAction controller="personArrestHistory" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personArrestHistory')}"/>


    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="['id','personId']"
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
