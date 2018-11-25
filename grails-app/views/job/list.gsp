<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'job.entities', default: 'Job List')}" />
    <g:set var="entity" value="${message(code: 'job.entity', default: 'Job')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Job List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="jobCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'job',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="jobSearchForm">
            <g:render template="/job/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['jobTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('jobSearchForm');_dataTables['jobTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="jobTable" searchFormName="jobSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="job" spaceBefore="true" hasRow="true" action="filter" serviceName="job">
    <el:dataTableAction controller="job" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show job')}" />
    <el:dataTableAction controller="job" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit job')}" />
    <el:dataTableAction controller="job" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete job')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>