<el:dataTable id="personEducationTable"
              searchFormName="personEducationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="personEducation"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="personEducation"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personEducation',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personEducation')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personEducation',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personEducation')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personEducation" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personEducation')}"/>
        <el:dataTableAction controller="personEducation" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personEducation')}"/>
    </g:else>
    <el:dataTableAction controller="personEducation" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personEducation')}"/>



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
