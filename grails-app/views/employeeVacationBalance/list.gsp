<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'employeeVacationBalance.entities', default: 'EmployeeVacationBalance List')}"/>
    <g:set var="entity" value="${message(code: 'employeeVacationBalance.entity', default: 'EmployeeVacationBalance')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'EmployeeVacationBalance List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="employeeVacationBalanceCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employeeVacationBalance',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employeeVacationBalanceSearchForm">
            <g:render template="/employeeVacationBalance/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeVacationBalanceTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('employeeVacationBalanceSearchForm');_dataTables['employeeVacationBalanceTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="employeeVacationBalanceTable" searchFormName="employeeVacationBalanceSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="employeeVacationBalance" spaceBefore="true"
              hasRow="true" action="filter" serviceName="employeeVacationBalance">
    <el:dataTableAction controller="employeeVacationBalance" action="show" actionParams="encodedId"
                        class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show employeeVacationBalance')}"/>
</el:dataTable>
</body>
</html>