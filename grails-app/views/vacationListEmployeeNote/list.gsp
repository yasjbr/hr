<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'vacationListEmployeeNote.entities', default: 'VacationListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'vacationListEmployeeNote.entity', default: 'VacationListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'VacationListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="vacationListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'vacationListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="vacationListEmployeeNoteSearchForm">
            <g:render template="/vacationListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['vacationListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('vacationListEmployeeNoteSearchForm');_dataTables['vacationListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="vacationListEmployeeNoteTable" searchFormName="vacationListEmployeeNoteSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="vacationListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="vacationListEmployeeNote">
    <el:dataTableAction controller="vacationListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show vacationListEmployeeNote')}" />
    <el:dataTableAction controller="vacationListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit vacationListEmployeeNote')}" />
    <el:dataTableAction controller="vacationListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete vacationListEmployeeNote')}" />
</el:dataTable>
</body>
</html>