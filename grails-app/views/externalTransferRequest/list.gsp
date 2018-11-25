<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'externalTransferRequest.entities', default: 'ExternalTransferRequest List')}"/>
    <g:set var="entity" value="${message(code: 'externalTransferRequest.entity', default: 'ExternalTransferRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'ExternalTransferRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="externalTransferRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'externalTransferRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="externalTransferRequestSearchForm">
            <g:render template="/externalTransferRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['externalTransferRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('externalTransferRequestSearchForm');_dataTables['externalTransferRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="externalTransferRequestTable" searchFormName="externalTransferRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="externalTransferRequest" spaceBefore="true"
              hasRow="true" action="filter" serviceName="externalTransferRequest">

    <el:dataTableAction controller="externalTransferRequest" action="show"
                        actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show externalTransferRequest')}"/>

    <el:dataTableAction controller="externalTransferRequest"
                        showFunction="manageExecuteEdit" action="edit" actionParams="encodedId"
                        class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit externalTransferRequest')}"/>

    <el:dataTableAction controller="externalTransferRequest"
                        showFunction="manageExecuteDelete" action="delete"
                        actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete externalTransferRequest')}"/>

    <el:dataTableAction controller="externalTransferRequest"
                        showFunction="viewTransferAndClearanceAction" action="addClearance"
                        actionParams="encodedId" class="icon-plus"
                        message="${message(code: 'externalTransferRequest.addClearance.label', default: 'clearance externalTransferRequest')}"/>

    <el:dataTableAction controller="externalTransferRequest"
                        showFunction="viewTransferAndClearanceAction" action="addTransfer"
                        actionParams="encodedId" class="green icon-move"
                        message="${message(code: 'externalTransferRequest.addTransfer.label', default: 'transfer externalTransferRequest')}"/>


    <el:dataTableAction controller="externalTransferRequest"
                        showFunction="viewCloseRequest" action="closeRequest"
                        actionParams="encodedId" class="blue icon-ok"
                        message="${message(code: 'externalTransferRequest.closeRequest.label', default: 'close externalTransferRequest')}"/>


    <el:dataTableAction
            functionName="openAttachmentModal"
            accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'attachment.entities')}"/>


    <el:dataTableAction controller="externalTransferRequest" action="goToList" showFunction="manageListLink2"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'externalTransferList.entities', default: 'externalTransferList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller: "request", action: "setInternalManagerialOrder")}"
                        showFunction="showSetOrderInfo" actionParams="['id', 'encodedId']"
                        class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>

</el:dataTable>
<g:render template="/request/script"/>
<script>
    function viewTransferAndClearanceAction(row) {
        return row.hasClearanceAndTransfer;
    }
    function viewCloseRequest(row) {
        return row.viewCloseRequest;
    }
    function manageListLink2(row) {
        if (row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}") {
            return true;
        }
        return false;
    }
</script>
</body>
</html>