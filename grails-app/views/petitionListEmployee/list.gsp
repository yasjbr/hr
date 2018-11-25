<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'petitionListEmployee.entities', default: 'PetitionListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'petitionListEmployee.entity', default: 'PetitionListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PetitionListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="petitionListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'petitionListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="petitionListEmployeeSearchForm">
            <g:render template="/petitionListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['petitionListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('petitionListEmployeeSearchForm');_dataTables['petitionListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="petitionListEmployeeTable" searchFormName="petitionListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="petitionListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="petitionListEmployee">
    <el:dataTableAction controller="petitionListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show petitionListEmployee')}" />
    <el:dataTableAction controller="petitionListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit petitionListEmployee')}" />
    <el:dataTableAction controller="petitionListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete petitionListEmployee')}" />
</el:dataTable>
</body>
</html>