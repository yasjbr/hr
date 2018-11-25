<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'employeeEvaluation.entities', default: 'EmployeeEvaluation List')}" />
    <g:set var="entity" value="${message(code: 'employeeEvaluation.entity', default: 'EmployeeEvaluation')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EmployeeEvaluation List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employeeEvaluationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employeeEvaluation',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employeeEvaluationSearchForm">
            <g:render template="/employeeEvaluation/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeEvaluationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employeeEvaluationSearchForm');_dataTables['employeeEvaluationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="employeeEvaluationTable" searchFormName="employeeEvaluationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="employeeEvaluation" spaceBefore="true" hasRow="true" action="filter" serviceName="employeeEvaluation">
    <el:dataTableAction controller="employeeEvaluation" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show employeeEvaluation')}" />
    <el:dataTableAction controller="employeeEvaluation" action="edit" actionParams="encodedId" showFunction="manageExecuteEdit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit employeeEvaluation')}" />
    <el:dataTableAction controller="employeeEvaluation" action="delete" showFunction="manageExecuteDelete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete employeeEvaluation')}" />
    <el:dataTableAction
            functionName="openAttachmentModal"
            accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
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

    function manageExecuteDelete(row) {
        if (row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}") {
            return true;
        }
        return false;
    }
    function manageExecuteEdit(row) {
        if (row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}") {
            return true;
        }
        return false;
    }

    function manageListLink(row) {
        if (row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST}" || row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST}" || row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}" || row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}") {
            return true;
        }
        return false;
    }


</script>
</body>
</html>
