<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'employeeInternalAssignation.entities', default: 'EmployeeInternalAssignation List')}" />
    <g:set var="entity" value="${message(code: 'employeeInternalAssignation.entity', default: 'EmployeeInternalAssignation')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EmployeeInternalAssignation List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employeeInternalAssignationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employeeInternalAssignation',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employeeInternalAssignationSearchForm">
            <g:render template="/employeeInternalAssignation/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeInternalAssignationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employeeInternalAssignationSearchForm');_dataTables['employeeInternalAssignationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/employeeInternalAssignation/dataTable" model="[title:title]"/>
</body>
</html>