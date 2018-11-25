<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'employeeViolation.entities', default: 'EmployeeViolation List')}" />
    <g:set var="entity" value="${message(code: 'employeeViolation.entity', default: 'EmployeeViolation')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EmployeeViolation List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employeeViolationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employeeViolation',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employeeViolationSearchForm">
            <g:render template="/employeeViolation/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeViolationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employeeViolationSearchForm');_dataTables['employeeViolationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<g:render template="/employeeViolation/dataTable" model="[title:title]"/>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>


<script>
    function viewEditAction(row) {
        return row.status == "${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.NEW}";
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

    function manageListLink(row) {
        if (row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}") {
            return true;
        }
        return false;
    }

</script>



</body>
</html>