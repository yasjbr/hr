<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'absence.entities', default: 'Absence List')}" />
    <g:set var="entity" value="${message(code: 'absence.entity', default: 'Absence')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Absence List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="absenceCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'absence',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="absenceSearchForm">
            <g:render template="/absence/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['absenceTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('absenceSearchForm');_dataTables['absenceTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="absenceTable" searchFormName="absenceSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="absence" spaceBefore="true" hasRow="true" action="filter" serviceName="absence">
    <el:dataTableAction controller="absence" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show absence')}" />
    <el:dataTableAction controller="absence" action="edit" actionParams="encodedId" class="blue icon-pencil" showFunction="manageExecuteEdit" message="${message(code:'default.edit.label',args:[entity],default:'edit absence')}" />
    <el:dataTableAction controller="absence" action="delete" actionParams="encodedId" class="red icon-trash" showFunction="manageExecuteDelete" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete absence')}" />
    %{--<el:dataTableAction controller="disciplinaryRequest" action="showDetails" type="modal-ajax" actionParams="disciplinaryRequestEncodedId" showFunction="viewShowDetailsAction" class="blue icon-list" message="${message(code:'disciplinaryRequest.viewDetails.label',default:'view details')}" />--}%

    <el:dataTableAction controller="returnFromAbsenceRequest" action="createNewRequest" showFunction="createReturnAction"
                        actionParams="absenceId" class="grey icon-back-in-time"
                        message="${message(code: 'returnFromAbsenceRequest.create.label', default: 'returnFromAbsenceRequest')}"/>

    <el:dataTableAction controller="returnFromAbsenceRequest" action="showRelatedRequest" showFunction="viewReturnAction"
                        actionParams="absenceId" class="grey icon-reply"
                        message="${message(code: 'returnFromAbsenceRequest.show.label', default: 'returnFromAbsenceRequest')}"/>

    <el:dataTableAction
            functionName="openAttachmentModal" accessUrl="${createLink(controller: 'attachment',action: 'filterAttachment')}"
            actionParams="id"
            class="blue icon-attach"
            type="function"
            message="${message(code: 'default.attachment.label')}"/>

    <el:dataTableAction accessUrl="${createLink(controller:"request", action:"setInternalManagerialOrder" )}"
                        showFunction="showSetOrderInfo" actionParams="['id','encodedId']" class="purple fa fa-certificate"
                        functionName="viewOrderInfoModal" type="function"
                        message="${message(code: 'request.setManagerialOrderInfo.label')}"/>

</el:dataTable>

<g:render template="/request/script"/>

<script>

    function createReturnAction(row) {
        if(!((row.violationStatusValue == "${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.CLOSED}") || (row.violationStatusValue == "${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.RETURNED}"))){
            return true
        }else{
            false
        }
    }

    function viewReturnAction(row) {
        if((row.violationStatusValue == "${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.CLOSED}") || (row.violationStatusValue == "${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumViolationStatus.RETURNED}")){
            return true
        }else{
            false
        }
    }
</script>

</body>
</html>