<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'serviceListEmployee.entities', default: 'ServiceListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'serviceListEmployee.entity', default: 'ServiceListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ServiceListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="serviceListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'serviceListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="serviceListEmployeeSearchForm">
            <g:render template="/serviceListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['serviceListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('serviceListEmployeeSearchForm');_dataTables['serviceListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="serviceListEmployeeTable" searchFormName="serviceListEmployeeSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="serviceListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="serviceListEmployee">
    <el:dataTableAction controller="serviceListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show serviceListEmployee')}" />
    <el:dataTableAction controller="serviceListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit serviceListEmployee')}" />
    <el:dataTableAction controller="serviceListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete serviceListEmployee')}" />
</el:dataTable>
</body>
</html>