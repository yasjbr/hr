<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'joinedEmployeeOperationalTasks.entities', default: 'JoinedEmployeeOperationalTasks List')}" />
    <g:set var="entity" value="${message(code: 'joinedEmployeeOperationalTasks.entity', default: 'JoinedEmployeeOperationalTasks')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'JoinedEmployeeOperationalTasks List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="joinedEmployeeOperationalTasksCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'joinedEmployeeOperationalTasks',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="joinedEmployeeOperationalTasksSearchForm">
            <g:render template="/joinedEmployeeOperationalTasks/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['joinedEmployeeOperationalTasksTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('joinedEmployeeOperationalTasksSearchForm');_dataTables['joinedEmployeeOperationalTasksTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="joinedEmployeeOperationalTasksTable" searchFormName="joinedEmployeeOperationalTasksSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="joinedEmployeeOperationalTasks" spaceBefore="true" hasRow="true" action="filter" serviceName="joinedEmployeeOperationalTasks">
    <el:dataTableAction controller="joinedEmployeeOperationalTasks" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show joinedEmployeeOperationalTasks')}" />
    <el:dataTableAction controller="joinedEmployeeOperationalTasks" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit joinedEmployeeOperationalTasks')}" />
    <el:dataTableAction controller="joinedEmployeeOperationalTasks" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete joinedEmployeeOperationalTasks')}" />
</el:dataTable>
</body>
</html>