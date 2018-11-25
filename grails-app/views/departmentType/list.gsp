<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'departmentType.entities', default: 'DepartmentType List')}" />
    <g:set var="entity" value="${message(code: 'departmentType.entity', default: 'DepartmentType')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'DepartmentType List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="departmentTypeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'departmentType',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="departmentTypeSearchForm">
            <g:render template="/departmentType/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['departmentTypeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('departmentTypeSearchForm');_dataTables['departmentTypeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="departmentTypeTable" searchFormName="departmentTypeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="departmentType" spaceBefore="true" hasRow="true" action="filter" serviceName="departmentType">
    <el:dataTableAction controller="departmentType" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show departmentType')}" />
    <el:dataTableAction controller="departmentType" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit departmentType')}" />
    <el:dataTableAction controller="departmentType" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete departmentType')}" />
</el:dataTable>
</body>
</html>