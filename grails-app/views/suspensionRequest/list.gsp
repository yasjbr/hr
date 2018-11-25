<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'suspensionRequest.entities', default: 'SuspensionRequest List')}"/>
    <g:set var="entity" value="${message(code: 'suspensionRequest.entity', default: 'SuspensionRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'SuspensionRequest List')}"/>
    <title>${title}</title>
    <g:render template="scripts"/>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="suspensionRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'suspensionRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="suspensionRequestSearchForm">
            <g:render template="/suspensionRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['suspensionRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('suspensionRequestSearchForm');_dataTables['suspensionRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="suspensionRequestTable" searchFormName="suspensionRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="suspensionRequest" spaceBefore="true" hasRow="true"
              action="filter" serviceName="suspensionRequest">

    <el:dataTableAction controller="suspensionRequest" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show suspensionRequest')}"/>


    <el:dataTableAction controller="suspensionRequest" action="edit" showFunction="manageExecuteEdit"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit suspensionRequest')}"/>

    <el:dataTableAction controller="suspensionRequest" action="delete" showFunction="manageExecuteDelete"
                        actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete suspensionRequest')}"/>

    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="id" controller="suspensionRequest" action="extensionRequestList"
            class="green icon-list-add" type="modal-ajax" showFunction="manageStopSuspension"
            message="${message(code: 'suspensionExtensionRequest.label', args: [entity], default: 'suspensionExtensionRequest')}"/>


    <el:dataTableAction controller="employmentServiceRequest" action="createReturnToService"
                        actionParams="['employeeId', 'serviceActionReasonName']"
                        class="red icon-stop-3" showFunction="manageStopSuspension"
                        message="${message(code: 'suspensionRequest.stopSuspension.label', args: [entity], default: 'suspensionStopRequest')}"/>



    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

</body>
</html>