<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus; ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'maritalStatusRequest.entities', default: 'MaritalStatusRequest List')}"/>
    <g:set var="entity" value="${message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'MaritalStatusRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="maritalStatusRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'maritalStatusRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="maritalStatusRequestSearchForm">
            <g:render template="/maritalStatusRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['maritalStatusRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('maritalStatusRequestSearchForm');_dataTables['maritalStatusRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="maritalStatusRequestTable" searchFormName="maritalStatusRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="maritalStatusRequest" spaceBefore="true" hasRow="true"
              action="filter" serviceName="maritalStatusRequest">
    <el:dataTableAction controller="maritalStatusRequest" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show maritalStatusRequest')}"/>
    <el:dataTableAction controller="maritalStatusRequest" action="edit" showFunction="manageExecuteEdit" actionParams="encodedId"
                        class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit maritalStatusRequest')}"/>
    <el:dataTableAction controller="maritalStatusRequest" action="delete" actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" showFunction="manageExecuteDelete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete maritalStatusRequest')}"/>
    <el:dataTableAction controller="request" action="editRequestCreate" showFunction="manageEditRequest" actionParams="encodedId"
                        class="blue icon-pencil" type="modal-ajax" preventCloseOutSide="true"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit maritalStatusRequest')}"/>

    <el:dataTableAction preventCloseOutSide="true" actionParams="encodedId" controller="request" action="cancelRequestCreate"
                        class="blue icon-stop-3" type="modal-ajax" showFunction="manageCancelRequest"
                        message="${message(code: 'maritalStatusRequest.cancel.maritalStatus.label', args: [entity], default: 'maritalStatusCancelRequest')}"/>

    <el:dataTableAction functionName="openAttachmentModal"
                        accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
                        actionParams="id" class="blue icon-attach" type="function"
                        message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction controller="maritalStatusRequest" action="goToList" showFunction="manageListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'maritalStatusList.label', default: 'maritalStatusList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

</body>
</html>