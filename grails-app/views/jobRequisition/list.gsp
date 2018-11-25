<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'jobRequisition.entities', default: 'JobRequisition List')}"/>
    <g:set var="entity" value="${message(code: 'jobRequisition.entity', default: 'JobRequisition')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'JobRequisition List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="jobRequisitionCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'jobRequisition', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="jobRequisitionSearchForm">
            <g:render template="/jobRequisition/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['jobRequisitionTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('jobRequisitionSearchForm');_dataTables['jobRequisitionTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="jobRequisitionTable" searchFormName="jobRequisitionSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="jobRequisition"
              spaceBefore="true" hasRow="true" action="filter" serviceName="jobRequisition" >
    <el:dataTableAction controller="jobRequisition" action="show" class="green icon-eye"
                        actionParams="encodedId"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show jobRequisition')}"/>
    <el:dataTableAction
            controller="jobRequisition"
            action="edit"
            showFunction="manageDeleteUpdateAction"
            actionParams="encodedId"
            class="blue icon-pencil"
            message="${message(code: 'default.edit.label', args: [entity], default: 'edit jobRequisition')}"/>
    <el:dataTableAction
            controller="jobRequisition"
            action="delete"
            class="red icon-trash"
            actionParams="encodedId"
            showFunction="manageDeleteUpdateAction"
            type="confirm-delete"
            message="${message(code: 'default.delete.label', args: [entity], default: 'delete jobRequisition')}"/>
    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>
</el:dataTable>

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


    function manageDeleteUpdateAction(row) {
        //QA : edit and delete the job requisition if the status is NEW only
        if (row.requisitionStatusValue) {
            if (row.requisitionStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}") {
                return true;
            }
            return false;
        }
        return true;
    }
</script>
</body>
</html>

