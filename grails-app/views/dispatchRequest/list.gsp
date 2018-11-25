<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus; ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'dispatchRequest.entities', default: 'DispatchRequest List')}"/>
    <g:set var="entity" value="${message(code: 'dispatchRequest.entity', default: 'DispatchRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'DispatchRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="dispatchRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'dispatchRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="dispatchRequestSearchForm">
            <g:render template="/dispatchRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['dispatchRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('dispatchRequestSearchForm');_dataTables['dispatchRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="dispatchRequestTable" searchFormName="dispatchRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="dispatchRequest" spaceBefore="true" hasRow="true"
              action="filter" serviceName="dispatchRequest">
    <el:dataTableAction controller="dispatchRequest" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show dispatchRequest')}"/>
    <el:dataTableAction controller="dispatchRequest" action="edit" showFunction="manageExecuteEdit" actionParams="encodedId"
                        class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit dispatchRequest')}"/>
    <el:dataTableAction controller="dispatchRequest" action="delete" actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" showFunction="manageExecuteDelete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete dispatchRequest')}"/>


    <el:dataTableAction controller="request" action="editRequestCreate" showFunction="manageEditRequest" actionParams="encodedId"
                        class="blue icon-pencil" type="modal-ajax" preventCloseOutSide="true"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit dispatchRequest')}"/>
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="encodedId" controller="request" action="extendRequestCreate"
            class="green icon-list-add" type="modal-ajax" showFunction="manageExtendRequest"
            message="${message(code: 'dispatchRequest.extend.dispatch.label', args: [entity], default: 'dispatchContinueRequest')}"/>

    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="encodedId" controller="request" action="stopRequestCreate"
            class="red icon-stop-3" type="modal-ajax" showFunction="manageStopRequest"
            message="${message(code: 'dispatchRequest.stop.dispatch.label', args: [entity], default: 'dispatchStopRequest')}"/>

    <el:dataTableAction preventCloseOutSide="true" actionParams="encodedId" controller="request" action="cancelRequestCreate"
                        class="blue icon-stop-3" type="modal-ajax" showFunction="manageCancelRequest"
                        message="${message(code: 'dispatchRequest.cancel.dispatch.label', args: [entity], default: 'dispatchCancelRequest')}"/>

    <el:dataTableAction functionName="openAttachmentModal"
                        accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
                        actionParams="id" class="blue icon-attach" type="function"
                        message="${message(code: 'default.attachment.label')}"/>


    <el:dataTableAction controller="dispatchRequest" action="goToList" showFunction="manageListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'dispatchListEmployee.label', default: 'dispatchList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

</body>
</html>