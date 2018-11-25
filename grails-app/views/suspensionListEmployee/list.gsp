<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'suspensionListEmployee.entities', default: 'SuspensionListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'suspensionListEmployee.entity', default: 'SuspensionListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'SuspensionListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="suspensionListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'suspensionListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="suspensionListEmployeeSearchForm">
            <g:render template="/suspensionListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['suspensionListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('suspensionListEmployeeSearchForm');_dataTables['suspensionListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="suspensionListEmployeeTable" searchFormName="suspensionListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="suspensionListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="suspensionListEmployee">
    <el:dataTableAction controller="suspensionListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show suspensionListEmployee')}" />
    <el:dataTableAction controller="suspensionListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit suspensionListEmployee')}" />
    <el:dataTableAction controller="suspensionListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete suspensionListEmployee')}" />
</el:dataTable>
</body>
</html>