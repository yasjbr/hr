<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'petitionListEmployeeNote.entities', default: 'PetitionListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'petitionListEmployeeNote.entity', default: 'PetitionListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PetitionListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="petitionListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'petitionListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="petitionListEmployeeNoteSearchForm">
            <g:render template="/petitionListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['petitionListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('petitionListEmployeeNoteSearchForm');_dataTables['petitionListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="petitionListEmployeeNoteTable" searchFormName="petitionListEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="petitionListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="petitionListEmployeeNote">
    <el:dataTableAction controller="petitionListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show petitionListEmployeeNote')}" />
    <el:dataTableAction controller="petitionListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit petitionListEmployeeNote')}" />
    <el:dataTableAction controller="petitionListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete petitionListEmployeeNote')}" />
</el:dataTable>
</body>
</html>