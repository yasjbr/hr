<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'vacationConfiguration.entities', default: 'VacationConfiguration List')}" />
    <g:set var="entity" value="${message(code: 'vacationConfiguration.entity', default: 'VacationConfiguration')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'VacationConfiguration List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="vacationConfigurationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'vacationConfiguration',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="vacationConfigurationSearchForm">
            <g:render template="/vacationConfiguration/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['vacationConfigurationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('vacationConfigurationSearchForm');_dataTables['vacationConfigurationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="vacationConfigurationTable" searchFormName="vacationConfigurationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="vacationConfiguration" spaceBefore="true" hasRow="true" action="filter" serviceName="vacationConfiguration">
    <el:dataTableAction controller="vacationConfiguration" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show vacationConfiguration')}" />
    <el:dataTableAction controller="vacationConfiguration" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit vacationConfiguration')}" />
    <el:dataTableAction controller="vacationConfiguration" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete vacationConfiguration')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>