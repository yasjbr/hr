<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'petitionRequest.entities', default: 'PetitionRequest List')}" />
    <g:set var="entity" value="${message(code: 'petitionRequest.entity', default: 'PetitionRequest')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PetitionRequest List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="petitionRequestCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'petitionRequest',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="petitionRequestSearchForm">
            <g:render template="/petitionRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['petitionRequestTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('petitionRequestSearchForm');_dataTables['petitionRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="petitionRequestTable" searchFormName="petitionRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="petitionRequest" spaceBefore="true" hasRow="true" action="filter" serviceName="petitionRequest">
    <el:dataTableAction controller="petitionRequest" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show petitionRequest')}" />
    <el:dataTableAction controller="petitionRequest" action="edit" showFunction="manageExecuteEdit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit petitionRequest')}" />
    <el:dataTableAction controller="petitionRequest" action="delete" showFunction="manageExecuteDelete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete petitionRequest')}" />
    <el:dataTableAction controller="disciplinaryRequest" action="show"
                        actionParams="disciplinaryEncodedId" class="pink icon-forward"
                        message="${message(code: 'disciplinaryRequest.label', default: 'disciplinaryRequest')}"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction controller="petitionRequest" action="goToList" showFunction="manageListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'petitionList.label', default: 'petitionList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>
<g:render template="/request/script"/>