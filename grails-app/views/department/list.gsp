<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'department.entities', default: 'Department List')}" />
    <g:set var="entity" value="${message(code: 'department.entity', default: 'Department')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Department List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="departmentCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'department',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="departmentSearchForm">
            <g:render template="/department/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['departmentTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('departmentSearchForm');_dataTables['departmentTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="departmentTable" searchFormName="departmentSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="department" spaceBefore="true" hasRow="true" action="filter" serviceName="department">
    <el:dataTableAction controller="department" actionParams="encodedId" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show department')}" />
    <el:dataTableAction controller="department" actionParams="encodedId" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit department')}" />
    <el:dataTableAction controller="department" actionParams="encodedId" action="delete" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete department')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>