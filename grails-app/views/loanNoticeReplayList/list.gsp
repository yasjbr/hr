<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'loanNoticeReplayList.entities', default: 'loanNoticeReplayList List')}"/>
    <g:set var="entity" value="${message(code: 'loanNoticeReplayList.entity', default: 'loanNoticeReplayList')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'loanNoticeReplayList List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="loanNoticeReplayListCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'loanNoticeReplayList', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="loanNoticeReplayListSearchForm">
            <g:render template="/loanNoticeReplayList/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanNoticeReplayListTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('loanNoticeReplayListSearchForm');_dataTables['loanNoticeReplayListTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="loanNoticeReplayListTable" searchFormName="loanNoticeReplayListSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanNoticeReplayList" spaceBefore="true" hasRow="true"
              action="filter" serviceName="loanNoticeReplayList">
    <el:dataTableAction controller="loanNoticeReplayList" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show loanNoticeReplayList')}"/>

    <el:dataTableAction controller="loanNoticeReplayList" action="edit" actionParams="encodedId"
                        showFunction="manageEditActions" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit loanNoticeReplayList')}"/>


    <el:dataTableAction controller="loanNoticeReplayList" action="delete" actionParams="encodedId"
                        showFunction="manageDeleteActions" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete loanNoticeReplayList')}"/>

    <el:dataTableAction controller="loanNoticeReplayList" action="manageLoanNoticeReplayList"
                        class="icon-cog"
                        message="${message(code: 'loanNoticeReplayList.manage.label')}"
                        actionParams="['encodedId']"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code:'attachment.entities')}"/>


</el:dataTable>

<g:render template="/attachment/attachmentCreateModal" model="[sharedTemplate:employee.autocomplete()]" />
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>

    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'shared');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('sharedOperationType', "${sharedOperationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    function manageEditActions(row) {
        if (row.currentStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}") {
            return true;
        }
        return false;
    }


    function manageListActions(row) {
        if (row.currentStatusValue != "${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CLOSED}") {
            return true;
        }
        return false;
    }

    function manageDeleteActions(row) {
        if (row.currentStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}" && row.transientData.numberOfCompetitorsValue == 0 ) {
            return true;
        }
        return false;
    }

</script>
</body>
</html>