<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'trainingRejectionReason.entities', default: 'TrainingRejectionReason List')}" />
    <g:set var="entity" value="${message(code: 'trainingRejectionReason.entity', default: 'TrainingRejectionReason')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'TrainingRejectionReason List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="trainingRejectionReasonCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'trainingRejectionReason',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="trainingRejectionReasonSearchForm">
            <g:render template="/trainingRejectionReason/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['trainingRejectionReasonTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('trainingRejectionReasonSearchForm');_dataTables['trainingRejectionReasonTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="trainingRejectionReasonTable" searchFormName="trainingRejectionReasonSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="trainingRejectionReason" spaceBefore="true" hasRow="true" action="filter" serviceName="trainingRejectionReason">
    <el:dataTableAction controller="trainingRejectionReason" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show trainingRejectionReason')}" />
    <el:dataTableAction controller="trainingRejectionReason" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit trainingRejectionReason')}" />
    <el:dataTableAction controller="trainingRejectionReason" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete trainingRejectionReason')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>