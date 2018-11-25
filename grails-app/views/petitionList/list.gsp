<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'petitionList.entities', default: 'petitionList List')}"/>
    <g:set var="entity" value="${message(code: 'petitionList.entity', default: 'petitionList')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'petitionList List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="petitionListCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'petitionList', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="petitionListSearchForm">
            <g:render template="/petitionList/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['petitionListTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('petitionListSearchForm');_dataTables['petitionListTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="petitionListTable" searchFormName="petitionListSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="petitionList" spaceBefore="true" hasRow="true"
              action="filter" serviceName="petitionList">
    <el:dataTableAction controller="petitionList" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show petitionList')}"/>

    <el:dataTableAction controller="petitionList" action="edit" actionParams="encodedId"
                        showFunction="manageEditActions"  class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit petitionList')}"/>

    <el:dataTableAction controller="petitionList" action="delete" actionParams="encodedId"
                        showFunction="manageDeleteActions" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete petitionList')}"/>

    <el:dataTableAction controller="petitionList" action="managePetitionList"
                        class="icon-cog"
                        message="${message(code: 'petitionList.manage.label')}"
                        actionParams="['encodedId']"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}" />

</el:dataTable>


<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>
    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject',"${referenceObject}" );
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList',  "${(attachmentTypeList?.encodeAsJSON())}");
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