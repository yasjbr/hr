<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'vacationListEmployee.entities', default: 'VacationListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'vacationListEmployee.entity', default: 'VacationListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'VacationListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="vacationListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'vacationListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="vacationListEmployeeSearchForm">
            <g:render template="/vacationListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['vacationListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('vacationListEmployeeSearchForm');_dataTables['vacationListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="vacationListEmployeeTable" searchFormName="vacationListEmployeeSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="vacationListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="vacationListEmployee">
    <el:dataTableAction controller="vacationListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show vacationListEmployee')}" />
    <el:dataTableAction controller="vacationListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit vacationListEmployee')}" />
    <el:dataTableAction controller="vacationListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete vacationListEmployee')}" />
</el:dataTable>
</body>
</html>