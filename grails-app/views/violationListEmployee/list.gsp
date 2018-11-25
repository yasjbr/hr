<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'violationListEmployee.entities', default: 'ViolationListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'violationListEmployee.entity', default: 'ViolationListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ViolationListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="violationListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'violationListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="violationListEmployeeSearchForm">
            <g:render template="/violationListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['violationListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('violationListEmployeeSearchForm');_dataTables['violationListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="violationListEmployeeTable" searchFormName="violationListEmployeeSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="violationListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="violationListEmployee">
    <el:dataTableAction controller="violationListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show violationListEmployee')}" />
    <el:dataTableAction controller="violationListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit violationListEmployee')}" />
    <el:dataTableAction controller="violationListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete violationListEmployee')}" />
</el:dataTable>
</body>
</html>