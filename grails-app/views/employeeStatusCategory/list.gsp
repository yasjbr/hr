<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'employeeStatusCategory.entities', default: 'EmployeeStatusCategory List')}" />
    <g:set var="entity" value="${message(code: 'employeeStatusCategory.entity', default: 'EmployeeStatusCategory')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EmployeeStatusCategory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employeeStatusCategoryCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employeeStatusCategory',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employeeStatusCategorySearchForm">
            <g:render template="/employeeStatusCategory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeStatusCategoryTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employeeStatusCategorySearchForm');_dataTables['employeeStatusCategoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="employeeStatusCategoryTable" searchFormName="employeeStatusCategorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="employeeStatusCategory" spaceBefore="true" hasRow="true" action="filter" serviceName="employeeStatusCategory">
    <el:dataTableAction controller="employeeStatusCategory" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show employeeStatusCategory')}" />
    <el:dataTableAction controller="employeeStatusCategory" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit employeeStatusCategory')}" />
    <el:dataTableAction controller="employeeStatusCategory" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete employeeStatusCategory')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>