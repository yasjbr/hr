<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'generalListEmployee.entities', default: 'GeneralListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'generalListEmployee.entity', default: 'GeneralListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'GeneralListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="generalListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'generalListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="generalListEmployeeSearchForm">
            <g:render template="/generalListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['generalListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('generalListEmployeeSearchForm');_dataTables['generalListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="generalListEmployeeTable" searchFormName="generalListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="generalListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="generalListEmployee">
    <el:dataTableAction controller="generalListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show generalListEmployee')}" />
    <el:dataTableAction controller="generalListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit generalListEmployee')}" />
    <el:dataTableAction controller="generalListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete generalListEmployee')}" />
</el:dataTable>
</body>
</html>