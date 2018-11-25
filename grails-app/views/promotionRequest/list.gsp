<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'promotionRequest.entities', default: 'promotionRequest List')}"/>
    <g:set var="entity"
           value="${message(code: 'promotionRequest.entity', default: 'promotionRequest')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'promotionRequest List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="promotionRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'promotionRequest', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="promotionRequestSearchForm">
            <g:render template="/promotionRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['promotionRequestTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('promotionRequestSearchForm');_dataTables['promotionRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="promotionRequestTable" searchFormName="promotionRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="promotionRequest" spaceBefore="true"
              hasRow="true" action="filter" serviceName="promotionRequest">
    <el:dataTableAction controller="promotionRequest" action="show" actionParams="encodedId"
                        class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show promotionRequest')}"/>

    <el:dataTableAction controller="promotionRequest" action="edit" showFunction="manageExecuteEdit"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit promotionRequest')}"/>

    <el:dataTableAction controller="promotionRequest" action="delete" showFunction="manageExecuteDelete"
                        actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete promotionRequest')}"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction controller="promotionRequest" action="goToList" showFunction="manageListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'promotionList.label', default: 'promotionList')}"/>


    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

</body>
</html>
