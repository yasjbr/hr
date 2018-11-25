<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'jobType.entities', default: 'JobType List')}" />
    <g:set var="entity" value="${message(code: 'jobType.entity', default: 'JobType')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'JobType List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="jobTypeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'jobType',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="jobTypeSearchForm">
            <g:render template="/jobType/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['jobTypeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('jobTypeSearchForm');_dataTables['jobTypeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="jobTypeTable" searchFormName="jobTypeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="jobType" spaceBefore="true" hasRow="true" action="filter" serviceName="jobType">
    <el:dataTableAction controller="jobType" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show jobType')}" />
    <el:dataTableAction controller="jobType" action="edit"  actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit jobType')}" />
    <el:dataTableAction controller="jobType" action="delete" actionParams="encodedId"  class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete jobType')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>