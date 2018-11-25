<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'joinedDepartmentOperationalTasks.entities', default: 'JoinedDepartmentOperationalTasks List')}" />
    <g:set var="entity" value="${message(code: 'joinedDepartmentOperationalTasks.entity', default: 'JoinedDepartmentOperationalTasks')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'JoinedDepartmentOperationalTasks List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="joinedDepartmentOperationalTasksCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'joinedDepartmentOperationalTasks',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="joinedDepartmentOperationalTasksSearchForm">
            <g:render template="/joinedDepartmentOperationalTasks/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['joinedDepartmentOperationalTasksTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('joinedDepartmentOperationalTasksSearchForm');_dataTables['joinedDepartmentOperationalTasksTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="joinedDepartmentOperationalTasksTable" searchFormName="joinedDepartmentOperationalTasksSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="joinedDepartmentOperationalTasks" spaceBefore="true" hasRow="true" action="filter" serviceName="joinedDepartmentOperationalTasks">
    <el:dataTableAction controller="joinedDepartmentOperationalTasks" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show joinedDepartmentOperationalTasks')}" />
    <el:dataTableAction controller="joinedDepartmentOperationalTasks" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit joinedDepartmentOperationalTasks')}" />
    <el:dataTableAction controller="joinedDepartmentOperationalTasks" action="delete" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete joinedDepartmentOperationalTasks')}" />
</el:dataTable>
</body>
</html>