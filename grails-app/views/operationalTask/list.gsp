<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'operationalTask.entities', default: 'OperationalTask List')}" />
    <g:set var="entity" value="${message(code: 'operationalTask.entity', default: 'OperationalTask')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'OperationalTask List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="operationalTaskCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'operationalTask',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="operationalTaskSearchForm">
            <g:render template="/operationalTask/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['operationalTaskTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('operationalTaskSearchForm');_dataTables['operationalTaskTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="operationalTaskTable" searchFormName="operationalTaskSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="operationalTask" spaceBefore="true" hasRow="true" action="filter" serviceName="operationalTask">
    <el:dataTableAction controller="operationalTask" action="show"  actionParams="encodedId"  class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show operationalTask')}" />
    <el:dataTableAction controller="operationalTask" action="edit" actionParams="encodedId"  class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit operationalTask')}" />
    <el:dataTableAction controller="operationalTask" action="delete" actionParams="encodedId"  class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete operationalTask')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>