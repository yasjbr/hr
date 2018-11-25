<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'loanNotice.entities', default: 'LoanNotice List')}" />
    <g:set var="entity" value="${message(code: 'loanNotice.entity', default: 'LoanNotice')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LoanNotice List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="loanNoticeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'loanNotice',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="loanNoticeSearchForm">
            <g:render template="/loanNotice/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanNoticeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanNoticeSearchForm');_dataTables['loanNoticeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="loanNoticeTable" searchFormName="loanNoticeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanNotice" spaceBefore="true" hasRow="true"
              action="filter" serviceName="loanNotice">

    <el:dataTableAction controller="loanNotice" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],default:'show loanNotice')}" />

    <el:dataTableAction controller="loanNotice" showFunction="viewEditAction" action="edit" actionParams="encodedId"
                        class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],
            default:'edit loanNotice')}" />

    <el:dataTableAction controller="loanNotice" showFunction="viewEditAction" action="delete" actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" message="${message(code:'default.delete.label',
            args:[entity],default:'delete loanNotice')}" />


    <el:dataTableAction controller="loanNotice" showFunction="viewEditAction" action="endNomination"
                        actionParams="encodedId" class="blue icon-ok"
                        message="${message(code:'loanNotice.endNomination.label',default:'end nomination')}" />


    <el:dataTableAction controller="loanNotice" showFunction="viewCloseAction" action="closeNomination"
                        actionParams="encodedId" class="blue icon-ok"
                        message="${message(code:'loanNotice.closeNomination.label',default:'close nomination')}" />


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
    function viewEditAction(row) {
        return row.status == "${ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus.UNDER_NOMINATION}";
    }

    function viewCloseAction(row) {
        return row.status == "${ps.gov.epsilon.hr.enums.v1.EnumLoanNoticeStatus.DONE_NOMINATION}";
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