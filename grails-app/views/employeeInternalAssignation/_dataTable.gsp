<el:dataTable id="employeeInternalAssignationTable" searchFormName="employeeInternalAssignationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="employeeInternalAssignation"
              spaceBefore="true" hasRow="true" action="filter"
              serviceName="employeeInternalAssignation"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">

        <el:dataTableAction accessUrl="${createLink(controller: 'employeeInternalAssignation',
                action: 'show')}" functionName="renderInLineShow" type="function"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employeeInternalAssignation')}"/>


        <el:dataTableAction showFunction="showData" accessUrl="${createLink(controller: 'employeeInternalAssignation',
                action: 'edit')}" functionName="renderInLineEdit" type="function"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit employeeInternalAssignation')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="employeeInternalAssignation" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employeeInternalAssignation')}"/>


        <el:dataTableAction showFunction="showData" controller="employeeInternalAssignation" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit employeeInternalAssignation')}"/>
    </g:else>

    <el:dataTableAction showFunction="showData" controller="employeeInternalAssignation" action="delete"
                        actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" message="${message(code: 'default.delete.label',
            args: [entity], default: 'delete employeeInternalAssignation')}"/>



    <el:dataTableAction
            functionName="openAttachmentModal"
            accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
            actionParams="['id','employeeId']"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'attachment.entities')}"/>

</el:dataTable>

<script type="text/javascript">
    function showData(row) {
        return row.canEdit
    }
</script>


<g:render template="/attachment/attachmentSharedTemplate" model="[
        referenceObject:referenceObject ,
        operationType:operationType,
        sharedOperationType:sharedOperationType,
        attachmentTypeList:attachmentTypeList,
        isNonSharedObject:true
]"/>
