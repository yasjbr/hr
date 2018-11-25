<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'employmentCategory.entities', default: 'EmploymentCategory List')}" />
    <g:set var="entity" value="${message(code: 'employmentCategory.entity', default: 'EmploymentCategory')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EmploymentCategory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employmentCategoryCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employmentCategory',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employmentCategorySearchForm">
            <g:render template="/employmentCategory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employmentCategoryTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employmentCategorySearchForm');_dataTables['employmentCategoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="employmentCategoryTable" searchFormName="employmentCategorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="employmentCategory" spaceBefore="true" hasRow="true" action="filter" serviceName="employmentCategory">
    <el:dataTableAction controller="employmentCategory" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show employmentCategory')}" />
    <el:dataTableAction controller="employmentCategory" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit employmentCategory')}" />
    <el:dataTableAction controller="employmentCategory" action="delete" actionParams="encodedId"  class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete employmentCategory')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>