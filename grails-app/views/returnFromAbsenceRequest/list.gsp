<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'returnFromAbsenceRequest.entities', default: 'ReturnFromAbsenceRequest List')}" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ReturnFromAbsenceRequest List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="returnFromAbsenceRequestCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceRequest',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="returnFromAbsenceRequestSearchForm">
            <g:render template="/returnFromAbsenceRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['returnFromAbsenceRequestTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('returnFromAbsenceRequestSearchForm');_dataTables['returnFromAbsenceRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="returnFromAbsenceRequestTable" searchFormName="returnFromAbsenceRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="returnFromAbsenceRequest" spaceBefore="true" hasRow="true" action="filter" serviceName="returnFromAbsenceRequest">
    <el:dataTableAction controller="returnFromAbsenceRequest" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show returnFromAbsenceRequest')}" />
    <el:dataTableAction controller="returnFromAbsenceRequest" action="edit"  showFunction="manageExecuteEdit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit returnFromAbsenceRequest')}" />
    <el:dataTableAction controller="returnFromAbsenceRequest" action="delete" actionParams="encodedId" showFunction="manageExecuteDelete" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete returnFromAbsenceRequest')}" />

    <el:dataTableAction controller="absence" action="show"
                        actionParams="absenceEncodedId" class="pink icon-forward"
                        message="${message(code: 'returnFromAbsenceRequest.absence.label', default: 'absence')}"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction controller="returnFromAbsenceRequest" action="goToList" showFunction="manageListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'returnFromAbsenceList.label', default: 'returnFromAbsenceList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>
<g:render template="/request/script"/>

</body>
</html>