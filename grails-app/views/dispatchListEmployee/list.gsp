<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'dispatchListEmployee.entities', default: 'DispatchListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'dispatchListEmployee.entity', default: 'DispatchListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'DispatchListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="dispatchListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'dispatchListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="dispatchListEmployeeSearchForm">
            <g:render template="/dispatchListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['dispatchListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('dispatchListEmployeeSearchForm');_dataTables['dispatchListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="dispatchListEmployeeTable" searchFormName="dispatchListEmployeeSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="dispatchListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="dispatchListEmployee">
    <el:dataTableAction controller="dispatchListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show dispatchListEmployee')}" />
    <el:dataTableAction controller="dispatchListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit dispatchListEmployee')}" />
    <el:dataTableAction controller="dispatchListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete dispatchListEmployee')}" />
</el:dataTable>
</body>
</html>