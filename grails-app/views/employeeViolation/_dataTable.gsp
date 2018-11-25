<el:dataTable id="employeeViolationTable" searchFormName="employeeViolationSearchForm"
              dataTableTitle="${title}" viewExtendButtons="${viewExtendButtons ?: 'true'}" isSingleSelect="true"
              hasCheckbox="true" widthClass="col-sm-12" controller="employeeViolation" spaceBefore="true" hasRow="true"
              action="filter" serviceName="employeeViolation" domainColumns="${domainColumns ?: "DOMAIN_COLUMNS"}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'employeeViolation', action: 'show')}"
                            actionParams="encodedId" functionName="renderInLineShow" type="function"
                            class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employeeViolation')}"/>
    </g:if>
    <g:elseif test="${!hideTools}">

        <el:dataTableAction controller="employeeViolation" action="show" actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show employeeViolation')}"/>


        <el:dataTableAction controller="employeeViolation" action="edit" showFunction="viewEditAction"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit employeeViolation')}"/>


        <el:dataTableAction controller="employeeViolation" action="delete" showFunction="viewEditAction"
                            actionParams="encodedId" class="red icon-trash"
                            type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete employeeViolation')}"/>



        <el:dataTableAction
                functionName="openAttachmentModal"
                accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
                actionParams="id"
                class="blue icon-attach"
                type="function"
                message="${message(code: 'attachment.entities')}"/>

        <el:dataTableAction controller="employeeViolation" action="goToList" showFunction="manageListLink"
                            actionParams="encodedId" class="icon-th-list-5"
                            message="${message(code: 'violationList.entities', default: 'violationList')}"/>

    </g:elseif>
</el:dataTable>