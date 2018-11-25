<el:dataTable id="employmentRecordTable" searchFormName="employmentRecordSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="employmentRecord" spaceBefore="true" hasRow="true"
              action="filter" serviceName="employmentRecord" domainColumns="${domainColumns}" >



    <g:if test="${isInLineActions}">

        <el:dataTableAction accessUrl="${createLink(controller: 'employmentRecord',
                action: 'show')}" functionName="renderInLineShow" type="function"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employmentRecord')}" />


        <el:dataTableAction accessUrl="${createLink(controller: 'employmentRecord',
                action: 'edit')}" functionName="renderInLineEdit" showFunction="viewEdit" type="function"
                            actionParams="encodedId"  class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit employmentRecord')}"/>

    </g:if>
    <g:else>
    <el:dataTableAction controller="employmentRecord" action="show"
                        actionParams="encodedId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],
                                default:'show employmentRecord')}" />


    <el:dataTableAction controller="employmentRecord" action="edit"
                        actionParams="encodedId" showFunction="viewEdit" class="blue icon-pencil"
                        message="${message(code:'default.edit.label',
                                args:[entity],default:'edit employmentRecord')}" />
    </g:else>


    <el:dataTableAction controller="employmentRecord" action="showInternalAssignation"
                        type="modal-ajax" actionParams="encodedId" class="blue icon-list"
                        message="${message(code:'employmentRecord.showInternalAssignation.label',default:'show internal assignation')}" />

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="['id','employeeId']"
            class="blue icon-attach"
            type="function"
            message="${message(code:'attachment.entities')}"/>


    <el:dataTableAction
            functionName="viewArr"
            actionParams="['id','employeeId']"
            class="blue icon-attach"
            type="function"
            message="${message(code:'attachment.entities')}"/>

</el:dataTable>


<script>
    function viewArr(id,id2) {
        alert(id);
        alert(id2);
    }
    function viewEdit(row) {
        return row.canEdit;
    }
</script>


<g:render template="/attachment/attachmentSharedTemplate" model="[
        referenceObject:referenceObject ,
        operationType:operationType,
        sharedOperationType:sharedOperationType,
        attachmentTypeList:attachmentTypeList,
        isNonSharedObject:true
]"/>
