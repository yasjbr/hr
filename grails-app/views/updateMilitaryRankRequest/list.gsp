<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'updateMilitaryRankRequest.entities', default: 'UpdateMilitaryRankRequest List')}" />
    <g:set var="entity" value="${message(code: 'updateMilitaryRankRequest.entity', default: 'UpdateMilitaryRankRequest')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'UpdateMilitaryRankRequest List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="updateMilitaryRankRequestCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'updateMilitaryRankRequest',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="updateMilitaryRankRequestSearchForm">
            <g:render template="/updateMilitaryRankRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['updateMilitaryRankRequestTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('updateMilitaryRankRequestSearchForm');_dataTables['updateMilitaryRankRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="updateMilitaryRankRequestTable" searchFormName="updateMilitaryRankRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="updateMilitaryRankRequest" spaceBefore="true" hasRow="true" action="filter" serviceName="updateMilitaryRankRequest">
    <el:dataTableAction controller="updateMilitaryRankRequest" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show updateMilitaryRankRequest')}" />

    <el:dataTableAction controller="updateMilitaryRankRequest" action="edit" showFunction="manageExecuteEdit"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit updateMilitaryRankRequest')}"/>

    <el:dataTableAction controller="updateMilitaryRankRequest" action="delete" showFunction="manageExecuteDelete"
                        actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete updateMilitaryRankRequest')}"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction controller="updateMilitaryRankRequest" action="goToList" showFunction="manageListLink2"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'promotionList.label', default: 'allowanceList')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>

</el:dataTable>

<g:render template="/request/script" />

<script>
    function manageListLink2(row) {
        if (row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST}" || row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST}" || row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}" || row.requestStatusValue == "${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}") {
            return true;
        }
        return false;
    }
</script>
</body>
</html>