<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'recallToService.entities', default: 'recallToService List')}" />
    <g:set var="entity" value="${message(code: 'recallToService.entity', default: 'recallToService')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'recallToService List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employmentServiceRequestCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employmentServiceRequest',action:'redirectReturnToService')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employmentServiceRequestSearchForm">
            <el:hiddenField name="requestType" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.RETURN_TO_SERVICE}" />
            <g:render template="/employmentServiceRequest/search" model="[endOfServiceFlag:'NO']"/>
            <el:formButton functionName="search" onClick="_dataTables['employmentServiceRequestTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employmentServiceRequestSearchForm');_dataTables['employmentServiceRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="employmentServiceRequestTable" searchFormName="employmentServiceRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="employmentServiceRequest" spaceBefore="true" hasRow="true" action="filter" serviceName="employmentServiceRequest">
    <el:dataTableAction controller="employmentServiceRequest" action="show" actionParams="encodedId"
                        class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show employmentServiceRequest')}"/>

    <el:dataTableAction controller="employmentServiceRequest" action="edit" showFunction="manageExecuteEdit"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit employmentServiceRequest')}"/>

    <el:dataTableAction controller="employmentServiceRequest" action="delete" showFunction="manageExecuteDelete"
                        actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete employmentServiceRequest')}"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction controller="employmentServiceRequest" action="goToList" showFunction="manageListLink"
                        actionParams="encodedId" class="icon-th-list-5"
                        message="${message(code: 'recallToServiceList.entities', default: 'employmentServiceRequest')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>
</el:dataTable>

<g:render template="/request/script"/>

<script>

    function manageExecuteExtendOrStop(row) {
        if (row.hasStopped == "true" || row.hasStopped == true) {
            return true;
        }
        return false;
    }
    function showExtendOrStopList(row) {
        if (row.hasStopped == false || row.hasStopped == "false") {
            return true;
        }
        return false;
    }

</script>

</body>
</html>