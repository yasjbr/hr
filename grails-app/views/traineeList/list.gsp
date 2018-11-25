<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'traineeList.entities', default: 'traineeList List')}"/>
    <g:set var="entity" value="${message(code: 'traineeList.entity', default: 'traineeList')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'traineeList List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="traineeListCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'traineeList', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="traineeListSearchForm">
            <g:render template="/traineeList/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['traineeListTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('traineeListSearchForm');_dataTables['traineeListTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="traineeListTable" searchFormName="traineeListSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="traineeList" spaceBefore="true" hasRow="true"
              action="filter" serviceName="traineeList">
    <el:dataTableAction controller="traineeList" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show traineeList')}"/>

    <el:dataTableAction controller="traineeList" action="edit" actionParams="encodedId"
                        showFunction="manageEditActions" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit traineeList')}"/>

    <el:dataTableAction controller="traineeList" action="delete" actionParams="encodedId"
                        showFunction="manageDeleteActions" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete traineeList')}"/>

    <el:dataTableAction controller="traineeList" action="manageTraineeList"
                        class="icon-cog"
                        message="${message(code: 'traineeList.manage.label')}"
                        actionParams="['encodedId']"/>

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