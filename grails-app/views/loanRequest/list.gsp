<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'loanRequest.entities', default: 'LoanRequest List')}" />
    <g:set var="entity" value="${message(code: 'loanRequest.entity', default: 'LoanRequest')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LoanRequest List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="loanRequestCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'loanRequest',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="loanRequestSearchForm">
            <g:render template="/loanRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanRequestTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanRequestSearchForm');_dataTables['loanRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="loanRequestTable" searchFormName="loanRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanRequest" spaceBefore="true" hasRow="true" action="filter" serviceName="loanRequest">

    <el:dataTableAction controller="loanRequest" action="show" actionParams="encodedId"
                        class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],default:'show loanRequest')}" />

    <el:dataTableAction controller="loanRequest" action="edit"
                        actionParams="encodedId" showFunction="manageExecuteEdit" class="blue icon-pencil"
                        message="${message(code:'default.edit.label',args:[entity],default:'edit loanRequest')}" />

    <el:dataTableAction controller="loanRequest" action="delete" actionParams="encodedId"
                        class="red icon-trash" showFunction="manageExecuteDelete" type="confirm-delete"
                        message="${message(code:'default.delete.label',args:[entity],default:'delete loanRequest')}" />

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code:'attachment.entities')}"/>


    <el:dataTableAction controller="loanRequest" action="goToList" showFunction="manageListLink2"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'loanList.entities', default: 'loanRequestList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>


<script>
    function manageListLink2(row) {
        if (row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}" || row.status == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}") {
            return true;
        }
        return false;
    }
</script>




</body>
</html>