<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'suspensionExtensionListEmployee.entities', default: 'SuspensionExtensionListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionListEmployee.entity', default: 'SuspensionExtensionListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'SuspensionExtensionListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="suspensionExtensionListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'suspensionExtensionListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="suspensionExtensionListEmployeeSearchForm">
            <g:render template="/suspensionExtensionListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['suspensionExtensionListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('suspensionExtensionListEmployeeSearchForm');_dataTables['suspensionExtensionListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="suspensionExtensionListEmployeeTable" searchFormName="suspensionExtensionListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="suspensionExtensionListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="suspensionExtensionListEmployee">
    <el:dataTableAction controller="suspensionExtensionListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show suspensionExtensionListEmployee')}" />
    <el:dataTableAction controller="suspensionExtensionListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit suspensionExtensionListEmployee')}" />
    <el:dataTableAction controller="suspensionExtensionListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete suspensionExtensionListEmployee')}" />
</el:dataTable>
</body>
</html>