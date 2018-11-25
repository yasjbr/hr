<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus; ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'allowanceRequest.entities', default: 'AllowanceRequest List')}"/>
    <g:set var="entity" value="${message(code: 'allowanceRequest.entity', default: 'AllowanceRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'AllowanceRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="allowanceRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'allowanceRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="allowanceRequestSearchForm">
            <g:render template="/allowanceRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['allowanceRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('allowanceRequestSearchForm');_dataTables['allowanceRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="allowanceRequestTable" searchFormName="allowanceRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="allowanceRequest" spaceBefore="true" hasRow="true"
              action="filter" serviceName="allowanceRequest">
    <el:dataTableAction controller="allowanceRequest" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show allowanceRequest')}"/>
    <el:dataTableAction controller="allowanceRequest" action="edit" showFunction="manageExecuteEdit" actionParams="encodedId"
                        class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit allowanceRequest')}"/>
    <el:dataTableAction controller="allowanceRequest" action="delete" actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" showFunction="manageExecuteDelete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete allowanceRequest')}"/>
    <el:dataTableAction controller="request" action="editRequestCreate" showFunction="manageEditRequest" actionParams="encodedId"
                        class="blue icon-pencil" type="modal-ajax" preventCloseOutSide="true"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit allowanceRequest')}"/>
    <el:dataTableAction preventCloseOutSide="true" actionParams="encodedId" controller="request" action="extendRequestCreate"
            class="green icon-list-add" type="modal-ajax" showFunction="manageExtendRequest"
            message="${message(code: 'allowanceRequest.continue.allowance.label', args: [entity], default: 'allowanceContinueRequest')}"/>

    <el:dataTableAction preventCloseOutSide="true" actionParams="encodedId" controller="request" action="stopRequestCreate"
            class="red icon-stop-3" type="modal-ajax" showFunction="manageStopRequest"
            message="${message(code: 'allowanceRequest.stop.allowance.label', args: [entity], default: 'allowanceStopRequest')}"/>

    <el:dataTableAction preventCloseOutSide="true" actionParams="encodedId" controller="request" action="cancelRequestCreate"
            class="blue icon-stop-3" type="modal-ajax" showFunction="manageCancelRequest"
            message="${message(code: 'allowanceRequest.cancel.allowance.label', args: [entity], default: 'allowanceCancelRequest')}"/>

    <el:dataTableAction functionName="openAttachmentModal"
                        accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
            actionParams="id" class="blue icon-attach" type="function"
            message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction controller="allowanceRequest" action="goToList" showFunction="manageListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'allowanceListEmployee.label', default: 'allowanceList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

</body>
</html>