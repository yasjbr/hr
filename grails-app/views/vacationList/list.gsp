<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'vacationList.entities', default: 'VacationList List')}"/>
    <g:set var="entity" value="${message(code: 'vacationList.entity', default: 'VacationList')}"/>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'VacationList List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="vacationListCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'vacationList', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="vacationListSearchForm">
            <g:render template="/vacationList/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['vacationListTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('vacationListSearchForm');_dataTables['vacationListTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="vacationListTable" searchFormName="vacationListSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="vacationList" spaceBefore="true" hasRow="true"
              action="filter" serviceName="vacationList">
    <el:dataTableAction controller="vacationList" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show vacationList')}"/>
    <el:dataTableAction controller="vacationList" action="edit" showFunction="ManageEditAction"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit vacationList')}"/>

    <el:dataTableAction controller="vacationList" action="delete" actionParams="encodedId"
                        showFunction="manageExecuteActions" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete vacationList')}"/>
    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: "default.attachment.label")}"/>
    <el:dataTableAction controller="vacationList" action="manageVacationList" class="icon-cog"
                        message="${message(code: 'vacationList.manage.label')}"
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
        if (row.currentStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}" && row.transientData.numberOfCompetitorsValue == 0) {
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