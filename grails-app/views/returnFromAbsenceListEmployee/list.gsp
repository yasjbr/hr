<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'returnFromAbsenceListEmployee.entities', default: 'ReturnFromAbsenceListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceListEmployee.entity', default: 'ReturnFromAbsenceListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ReturnFromAbsenceListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="returnFromAbsenceListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="returnFromAbsenceListEmployeeSearchForm">
            <g:render template="/returnFromAbsenceListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['returnFromAbsenceListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('returnFromAbsenceListEmployeeSearchForm');_dataTables['returnFromAbsenceListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="returnFromAbsenceListEmployeeTable" searchFormName="returnFromAbsenceListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="returnFromAbsenceListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="returnFromAbsenceListEmployee">
    <el:dataTableAction controller="returnFromAbsenceListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show returnFromAbsenceListEmployee')}" />
    <el:dataTableAction controller="returnFromAbsenceListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit returnFromAbsenceListEmployee')}" />
    <el:dataTableAction controller="returnFromAbsenceListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete returnFromAbsenceListEmployee')}" />
</el:dataTable>
</body>
</html>