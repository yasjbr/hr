<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'generalListEmployeeNote.entities', default: 'GeneralListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'generalListEmployeeNote.entity', default: 'GeneralListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'GeneralListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="generalListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'generalListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="generalListEmployeeNoteSearchForm">
            <g:render template="/generalListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['generalListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('generalListEmployeeNoteSearchForm');_dataTables['generalListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="generalListEmployeeNoteTable" searchFormName="generalListEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="generalListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="generalListEmployeeNote">
    <el:dataTableAction controller="generalListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show generalListEmployeeNote')}" />
    <el:dataTableAction controller="generalListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit generalListEmployeeNote')}" />
    <el:dataTableAction controller="generalListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete generalListEmployeeNote')}" />
</el:dataTable>
</body>
</html>