<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'jobCategory.entities', default: 'JobCategory List')}"/>
    <g:set var="entity" value="${message(code: 'jobCategory.entity', default: 'JobCategory')}"/>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'JobCategory List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="jobCategoryCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'jobCategory', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="jobCategorySearchForm">
            <g:render template="/jobCategory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['jobCategoryTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('jobCategorySearchForm');_dataTables['jobCategoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="jobCategoryTable" searchFormName="jobCategorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="jobCategory" spaceBefore="true" hasRow="true"
              action="filter" serviceName="jobCategory">
    <el:dataTableAction controller="jobCategory" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show jobCategory')}"/>
    <el:dataTableAction controller="jobCategory" action="edit" actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit jobCategory')}"/>
    <el:dataTableAction controller="jobCategory" action="delete" actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" showFunction="manageExecuteDelete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete jobCategory')}"/>
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>