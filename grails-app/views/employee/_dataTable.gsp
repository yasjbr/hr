<cache:dataTable id="employeeTable" searchFormName="employeeSearchForm"
              dataTableTitle="${title}" hasCopyId="true" controlDomainColumns="DOMAIN_COLUMNS_DT_CONTROL"
              hasCheckbox="${preventDataTableTools?"false":"true"}"
              viewExtendButtons="${preventDataTableTools?"false":"true"}"
              widthClass="col-sm-12" controller="employee" spaceBefore="true" hasRow="true"
              action="filter" serviceName="employee" domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
            <el:dataTableAction accessUrl="${createLink(controller: 'departmentContactInfo',action: 'show')}"
                                functionName="renderInLineShow" actionParams="id"  type="function" class="green icon-eye"
                                message="${message(code: 'default.show.label', args: [entity], default: 'show employee')}"/>
    </g:if>
    <g:else>
        <el:dataTableAction controller="employee" action="show" actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show employee')}"/>
        <el:dataTableAction controller="employee" action="edit" actionParams="encodedId" showFunction="manageExecuteEdit"
                            class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit employee')}"/>
        <el:dataTableAction
                functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
                actionParams="id"
                class="blue icon-attach"
                type="function"
                message="${message(code:'attachment.entities')}"/>
    </g:else>



</cache:dataTable>

<g:if test="${!isReadOnly && !params['isReadOnly']}">
    <g:render template="/attachment/attachmentCreateModal"/>
    <g:render template="/attachment/attachmentListModal"/>
    <g:render template="/attachment/attachmentPreviewModal"/>
    <g:render template="/attachment/attachmentViewModal"/>
    <g:render template="/attachment/attachmentEditModal"/>

    <script>
        function openAttachmentModal(row) {
            $("#attachmentListModal").attr('parentId', row);
            $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
            $("#attachmentListModal").attr('parentType', 'normal');
            $("#attachmentListModal").attr('operationType', "${operationType}");
            $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
            $("#attachmentListModal").attr('callBackFunction', '');
            $("#attachmentListModal").modal('show');
        }
    </script>
</g:if>