<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'externalReceivedTransferredPerson.entities', default: 'ExternalReceivedTransferredPerson List')}" />
    <g:set var="entity" value="${message(code: 'externalReceivedTransferredPerson.entity', default: 'ExternalReceivedTransferredPerson')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ExternalReceivedTransferredPerson List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="externalReceivedTransferredPersonCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'externalReceivedTransferredPerson',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="externalReceivedTransferredPersonSearchForm">
            <g:render template="/externalReceivedTransferredPerson/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['externalReceivedTransferredPersonTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('externalReceivedTransferredPersonSearchForm');_dataTables['externalReceivedTransferredPersonTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="externalReceivedTransferredPersonTable" searchFormName="externalReceivedTransferredPersonSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="externalReceivedTransferredPerson" spaceBefore="true" hasRow="true" action="filter" serviceName="externalReceivedTransferredPerson">
    <el:dataTableAction controller="externalReceivedTransferredPerson" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show externalReceivedTransferredPerson')}" />
    <el:dataTableAction controller="externalReceivedTransferredPerson" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit externalReceivedTransferredPerson')}" />
    <el:dataTableAction controller="externalReceivedTransferredPerson" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete externalReceivedTransferredPerson')}" />

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