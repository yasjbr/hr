<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus; ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'childRequest.entities', default: 'ChildRequest List')}"/>
    <g:set var="entity" value="${message(code: 'childRequest.entity', default: 'ChildRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'ChildRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="childRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'childRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="childRequestSearchForm">
            <g:render template="/childRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['childRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('childRequestSearchForm');_dataTables['childRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="childRequestTable" searchFormName="childRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="childRequest" spaceBefore="true" hasRow="true"
              action="filter" serviceName="childRequest">
    <el:dataTableAction controller="childRequest" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show childRequest')}"/>
    <el:dataTableAction controller="childRequest" action="edit" showFunction="manageExecuteEdit" actionParams="encodedId"
                        class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit childRequest')}"/>
    <el:dataTableAction controller="childRequest" action="delete" actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" showFunction="manageExecuteDelete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete childRequest')}"/>


    <el:dataTableAction controller="request" action="editRequestCreate" showFunction="manageEditRequest" actionParams="encodedId"
                        class="blue icon-pencil" type="modal-ajax" preventCloseOutSide="true"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit childRequest')}"/>


    <el:dataTableAction preventCloseOutSide="true" actionParams="encodedId" controller="request" action="cancelRequestCreate"
                        class="blue icon-stop-3" type="modal-ajax" showFunction="manageCancelRequest"
                        message="${message(code: 'childRequest.cancel.child.label', args: [entity], default: 'childCancelRequest')}"/>

    <el:dataTableAction functionName="openAttachmentModal"
                        accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"
                        actionParams="id" class="blue icon-attach" type="function"
                        message="${message(code: 'default.attachment.label')}"/>


    <el:dataTableAction controller="childRequest" action="goToList" showFunction="manageListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'childListEmployee.label', default: 'childList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

</body>
</html>