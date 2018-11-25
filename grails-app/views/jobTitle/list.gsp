<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'jobTitle.entities', default: 'JobTitle List')}" />
    <g:set var="entity" value="${message(code: 'jobTitle.entity', default: 'JobTitle')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'JobTitle List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="jobTitleCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'jobTitle',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="jobTitleSearchForm">
            <g:render template="/jobTitle/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['jobTitleTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('jobTitleSearchForm');_dataTables['jobTitleTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="jobTitleTable" searchFormName="jobTitleSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="jobTitle" spaceBefore="true" hasRow="true" action="filter" serviceName="jobTitle">
    <el:dataTableAction controller="jobTitle" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show jobTitle')}" />
    <el:dataTableAction controller="jobTitle" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit jobTitle')}" />
    <el:dataTableAction controller="jobTitle" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete jobTitle')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>