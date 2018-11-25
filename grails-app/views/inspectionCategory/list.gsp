<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'inspectionCategory.entities', default: 'InspectionCategory List')}"/>
    <g:set var="entity" value="${message(code: 'inspectionCategory.entity', default: 'InspectionCategory')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'InspectionCategory List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="inspectionCategoryCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'inspectionCategory', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="inspectionCategorySearchForm" id="inspectionCategorySearchForm">
            <g:render template="/inspectionCategory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['inspectionCategoryTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('inspectionCategorySearchForm');_dataTables['inspectionCategoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="inspectionCategoryTable" searchFormName="inspectionCategorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="inspectionCategory" spaceBefore="true" hasRow="true"
              action="filter" serviceName="inspectionCategory">
    <el:dataTableAction controller="inspectionCategory" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show inspectionCategory')}"/>
    <el:dataTableAction controller="inspectionCategory" action="edit" actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code: 'default.edit.label', args: [entity], default: 'edit inspectionCategory')}"/>
    <el:dataTableAction controller="inspectionCategory" action="delete" actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" showFunction="manageExecuteDelete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete inspectionCategory')}"/>
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>
