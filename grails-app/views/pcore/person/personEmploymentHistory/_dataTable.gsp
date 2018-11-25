<el:dataTable id="personEmploymentHistoryTable"
              searchFormName="personEmploymentHistorySearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personEmploymentHistory"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="personEmploymentHistory"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personEmploymentHistory', action: 'show')}"
                            functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personEmploymentHistory')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personEmploymentHistory', action: 'edit')}"
                            functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personEmploymentHistory')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personEmploymentHistory" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personEmploymentHistory')}"/>
        <el:dataTableAction controller="personEmploymentHistory" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personEmploymentHistory')}"/>
    </g:else>
    <el:dataTableAction controller="personEmploymentHistory" action="delete" class="red icon-trash"
                        type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personEmploymentHistory')}"/>

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
