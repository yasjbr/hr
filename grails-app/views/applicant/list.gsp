<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'applicant.entities', default: 'Applicant List')}"/>
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'Applicant')}"/>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'Applicant List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="applicantCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'applicant', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="applicantSearchForm">
            <el:hiddenField name="withRemotingValues" value="true"/>
            <g:render template="/applicant/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['applicantTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('applicantSearchForm');_dataTables['applicantTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/><el:row/>
<el:dataTable id="applicantTable" searchFormName="applicantSearchForm"
              dataTableTitle="${title}"
              isScrollable="false"
              hasCheckbox="true" widthClass="col-sm-12" controller="applicant" spaceBefore="true"
              hasRow="true" action="filter" serviceName="applicant">

    <el:dataTableAction controller="applicant" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show applicant')}"/>


    <el:dataTableAction
            functionName="openAttachmentModal"
            accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>
    <el:dataTableAction controller="applicant" action="edit" actionParams="encodedId" showFunction="manageExecuteEdit"
                        class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit applicant')}"/>
    <el:dataTableAction controller="applicant" action="delete" actionParams="encodedId"
                        showFunction="manageDeleteActions" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete applicant')}"/>
    <el:dataTableAction controller="applicant" action="goToListTrainee" showFunction="manageTraineeListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'traineeList.label', default: 'traineeList')}"/>

    <el:dataTableAction controller="applicant" action="goToListRecruitment" showFunction="manageRecruitmentListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'recruitmentList.label', default: 'recruitmentList')}"/>

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

    function manageRecruitmentListLink(row) {
        return row.hasRecruitmentList;
    }

    function manageTraineeListLink(row) {
        return row.hasTraineeList;
    }

    function manageDeleteActions(row) {
        return(row.currentStatus == "${ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus.NEW}");
    }

</script>
