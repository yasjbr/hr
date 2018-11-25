<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'trainingClassification.entities', default: 'TrainingClassification List')}" />
    <g:set var="entity" value="${message(code: 'trainingClassification.entity', default: 'TrainingClassification')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'TrainingClassification List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="trainingClassificationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'trainingClassification',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="trainingClassificationSearchForm">
            <g:render template="/trainingClassification/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['trainingClassificationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('trainingClassificationSearchForm');_dataTables['trainingClassificationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="trainingClassificationTable" searchFormName="trainingClassificationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="trainingClassification" spaceBefore="true" hasRow="true" action="filter" serviceName="trainingClassification">
    <el:dataTableAction controller="trainingClassification" actionParams="encodedId" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show trainingClassification')}" />
    <el:dataTableAction controller="trainingClassification" actionParams="encodedId" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit trainingClassification')}" />
    <el:dataTableAction controller="trainingClassification" actionParams="encodedId" action="delete" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete trainingClassification')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>