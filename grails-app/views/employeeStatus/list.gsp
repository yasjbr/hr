<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'employeeStatus.entities', default: 'EmployeeStatus List')}" />
    <g:set var="entity" value="${message(code: 'employeeStatus.entity', default: 'EmployeeStatus')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EmployeeStatus List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employeeStatusCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employeeStatus',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employeeStatusSearchForm">
            <g:render template="/employeeStatus/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeStatusTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employeeStatusSearchForm');_dataTables['employeeStatusTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="employeeStatusTable" searchFormName="employeeStatusSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="employeeStatus" spaceBefore="true" hasRow="true" action="filter" serviceName="employeeStatus">
    <el:dataTableAction controller="employeeStatus" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show employeeStatus')}" />
    <el:dataTableAction controller="employeeStatus" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit employeeStatus')}" />
    <el:dataTableAction controller="employeeStatus" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete employeeStatus')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>