<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'loanRequestRelatedPerson.entities', default: 'LoanRequestRelatedPerson List')}" />
    <g:set var="entity" value="${message(code: 'loanRequestRelatedPerson.entity', default: 'LoanRequestRelatedPerson')}" />
    <g:set var="entityRequest" value="${message(code: 'loanRequest.entity', default: 'LoanRequestRelatedPerson')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LoanRequestRelatedPerson List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="loanRequestRelatedPersonCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetBody>
        <el:form action="#" name="loanRequestRelatedPersonSearchForm">
            <g:render template="/loanRequestRelatedPerson/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanRequestRelatedPersonTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanRequestRelatedPersonSearchForm');_dataTables['loanRequestRelatedPersonTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="loanRequestRelatedPersonTable" searchFormName="loanRequestRelatedPersonSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanRequestRelatedPerson"
              spaceBefore="true" hasRow="true" action="filter"
              serviceName="loanRequestRelatedPerson">

    <el:dataTableAction controller="loanRequest" action="show"
                        actionParams="encodedRequestId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entityRequest],default:'show loanRequestRelatedPerson')}" />

    <el:dataTableAction controller="employee" action="createNewEmployee"
                        actionParams="personId" class="blue icon-plus"
                        message="${message(code:'loanRequestRelatedPerson.addProfile.label',default:'add profile')}" />


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

</script>

</body>
</html>