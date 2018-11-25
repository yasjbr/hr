<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'loanNominatedEmployee.entities', default: 'LoanNominatedEmployee List')}" />
    <g:set var="entity" value="${message(code: 'loanNominatedEmployee.entity', default: 'LoanNominatedEmployee')}" />
    <g:set var="entityEndorseOrder" value="${message(code: 'endorseOrder.entity', default: 'endorseOrder')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LoanNominatedEmployee List')}" />
    <title>${message(code: 'endorseOrder.document.label', default: 'document endorseOrder')}</title>
</head>
<body>
<msg:page />
<msg:warning label="${message(code:'endorseOrder.endorseOrderWarning.label')}" />
<lay:collapseWidget id="loanNominatedEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetBody>
        <el:form action="#" name="loanNominatedEmployeeSearchForm">
            <el:hiddenField name="justApprovedEmployee" value="true" />
            <g:render template="/loanNominatedEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanNominatedEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanNominatedEmployeeSearchForm');_dataTables['loanNominatedEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row />
<el:dataTable id="loanNominatedEmployeeTable" searchFormName="loanNominatedEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanNominatedEmployee"
              spaceBefore="true" hasRow="true" action="filter" serviceName="loanNominatedEmployee">

    <el:dataTableAction controller="endorseOrder" action="create"
                        actionParams="loanNominatedEmployeeEncodedId" class="blue icon-plus" showFunction="manageCreateActions"
                        message="${message(code:'default.create.label',args:[entityEndorseOrder],default:'create endorseOrder')}" />


    <el:dataTableAction controller="endorseOrder" action="show" actionParams="endorseOrderEncodedId"
                        class="green icon-eye" showFunction="manageShowActions"
                        message="${message(code:'default.show.label',args:[entityEndorseOrder],default:'show endorseOrder')}" />


    <el:dataTableAction controller="endorseOrder" action="edit"
                        actionParams="['endorseOrderEncodedId','loanNominatedEmployeeEncodedId']" class="blue icon-pencil"
                        showFunction="manageEditActions"
                        message="${message(code:'default.edit.label',args:[entityEndorseOrder],default:'edit endorseOrder')}" />

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code:'attachment.entities')}"/>

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

    function manageShowActions(row) {
        return row.endorseOrderEncodedId;
    }

    function manageCreateActions(row) {
        return !row.endorseOrderEncodedId;
    }

    function manageEditActions(row) {
        return row.allowEditEndorseOrder;
    }

</script>
</body>
</html>