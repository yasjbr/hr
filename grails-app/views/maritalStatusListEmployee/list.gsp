<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'maritalStatusListEmployee.entities', default: 'MaritalStatusListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'maritalStatusListEmployee.entity', default: 'MaritalStatusListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'MaritalStatusListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="maritalStatusListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'maritalStatusListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="maritalStatusListEmployeeSearchForm">
            <g:render template="/maritalStatusListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['maritalStatusListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('maritalStatusListEmployeeSearchForm');_dataTables['maritalStatusListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="maritalStatusListEmployeeTable" searchFormName="maritalStatusListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="maritalStatusListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="maritalStatusListEmployee">
    <el:dataTableAction controller="maritalStatusListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show maritalStatusListEmployee')}" />
    <el:dataTableAction controller="maritalStatusListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit maritalStatusListEmployee')}" />
    <el:dataTableAction controller="maritalStatusListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete maritalStatusListEmployee')}" />
</el:dataTable>
</body>
</html>