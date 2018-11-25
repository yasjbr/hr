<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'returnFromAbsenceListEmployeeNote.entities', default: 'ReturnFromAbsenceListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceListEmployeeNote.entity', default: 'ReturnFromAbsenceListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ReturnFromAbsenceListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="returnFromAbsenceListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="returnFromAbsenceListEmployeeNoteSearchForm">
            <g:render template="/returnFromAbsenceListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['returnFromAbsenceListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('returnFromAbsenceListEmployeeNoteSearchForm');_dataTables['returnFromAbsenceListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="returnFromAbsenceListEmployeeNoteTable" searchFormName="returnFromAbsenceListEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="returnFromAbsenceListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="returnFromAbsenceListEmployeeNote">
    <el:dataTableAction controller="returnFromAbsenceListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show returnFromAbsenceListEmployeeNote')}" />
    <el:dataTableAction controller="returnFromAbsenceListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit returnFromAbsenceListEmployeeNote')}" />
    <el:dataTableAction controller="returnFromAbsenceListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete returnFromAbsenceListEmployeeNote')}" />
</el:dataTable>
</body>
</html>