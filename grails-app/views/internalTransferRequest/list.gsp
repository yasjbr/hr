<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'internalTransferRequest.entities', default: 'InternalTransferRequest List')}"/>
    <g:set var="entity" value="${message(code: 'internalTransferRequest.entity', default: 'InternalTransferRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'InternalTransferRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="internalTransferRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'internalTransferRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="internalTransferRequestSearchForm">
            <g:render template="/internalTransferRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['internalTransferRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('internalTransferRequestSearchForm');_dataTables['internalTransferRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="internalTransferRequestTable" searchFormName="internalTransferRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="internalTransferRequest" spaceBefore="true"
              hasRow="true"
              action="filter" serviceName="internalTransferRequest">

    <el:dataTableAction controller="internalTransferRequest" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show internalTransferRequest')}"/>

    <el:dataTableAction controller="internalTransferRequest" action="edit" showFunction="manageExecuteEdit" actionParams="encodedId"
                        class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit internalTransferRequest')}"/>

    <el:dataTableAction controller="internalTransferRequest" action="delete" actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" showFunction="manageExecuteDelete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete internalTransferRequest')}"/>

    <el:dataTableAction controller="internalTransferRequest"
                        showFunction="viewCloseRequest" action="close"
                        actionParams="encodedId" class="blue icon-ok"
                        message="${message(code: 'externalTransferRequest.closeRequest.label', default: 'close externalTransferRequest')}"/>

    <el:dataTableAction functionName="openAttachmentModal"
                        accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
                        actionParams="id"
                        class="blue icon-attach"
                        type="function"
                        message="${message(code: 'attachment.entities')}"/>

    <el:dataTableAction accessUrl="${createLink(controller: "request", action: "setInternalManagerialOrder")}"
                        showFunction="showSetOrderInfo" actionParams="['id', 'encodedId']"
                        class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

<script>
    function viewCloseRequest(row) {
        return row.viewCloseRequest;
    }
</script>
</body>
</html>