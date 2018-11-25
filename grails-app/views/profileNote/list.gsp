<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'profileNote.entities', default: 'ProfileNote List')}" />
    <g:set var="entity" value="${message(code: 'profileNote.entity', default: 'ProfileNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ProfileNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="profileNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'profileNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="profileNoteSearchForm">
            <g:render template="/profileNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['profileNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('profileNoteSearchForm');_dataTables['profileNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="profileNoteTable" searchFormName="profileNoteSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="profileNote" spaceBefore="true" hasRow="true" action="filter" serviceName="profileNote">
    <el:dataTableAction controller="profileNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show profileNote')}" />
    <el:dataTableAction controller="profileNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit profileNote')}" />
    <el:dataTableAction controller="profileNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete profileNote')}" />
</el:dataTable>
</body>
</html>