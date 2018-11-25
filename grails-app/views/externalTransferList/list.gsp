<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'externalTransferList.entities', default: 'ExternalTransferList List')}"/>
    <g:set var="entity" value="${message(code: 'externalTransferList.entity', default: 'ExternalTransferList')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'ExternalTransferList List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="externalTransferListCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'externalTransferList', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="externalTransferListSearchForm">
            <g:render template="/externalTransferList/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['externalTransferListTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('externalTransferListSearchForm');_dataTables['externalTransferListTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="externalTransferListTable" searchFormName="externalTransferListSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="externalTransferList" spaceBefore="true"
              hasRow="true"
              action="filter" serviceName="externalTransferList">
    <el:dataTableAction controller="externalTransferList" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show externalTransferList')}"/>
    <el:dataTableAction controller="externalTransferList" action="edit" showFunction="ManageEditAction"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit externalTransferList')}"/>
    <el:dataTableAction controller="externalTransferList" action="delete" actionParams="encodedId"
                        showFunction="manageExecuteActions" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete externalTransferList')}"/>
    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: "default.attachment.label")}"/>
    <el:dataTableAction controller="externalTransferList" action="manageExternalTransferList" class="icon-cog"
                        message="${message(code: 'externalTransferList.manage.label')}"
                        actionParams="encodedId"/>

</el:dataTable>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>
    //to allow delete for recruitment list that current status value is CREATED
    function manageExecuteActions(row) {
        if (row.currentStatus.correspondenceListStatus == "منشأة") {
            return true;
        }
        return false;
    }

    /*to prevent edit when the list status is not created*/
    function ManageEditAction(row) {
        if (row.currentStatus.correspondenceListStatus == "منشأة") {
            return true;
        }
        return false;
    }

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
</body>
</html>