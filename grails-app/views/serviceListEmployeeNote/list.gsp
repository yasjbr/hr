<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'serviceListEmployeeNote.entities', default: 'ServiceListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'serviceListEmployeeNote.entity', default: 'ServiceListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ServiceListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="serviceListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'serviceListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="serviceListEmployeeNoteSearchForm">
            <g:render template="/serviceListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['serviceListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('serviceListEmployeeNoteSearchForm');_dataTables['serviceListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="serviceListEmployeeNoteTable" searchFormName="serviceListEmployeeNoteSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="serviceListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="serviceListEmployeeNote">
    <el:dataTableAction controller="serviceListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show serviceListEmployeeNote')}" />
    <el:dataTableAction controller="serviceListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit serviceListEmployeeNote')}" />
    <el:dataTableAction controller="serviceListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete serviceListEmployeeNote')}" />
</el:dataTable>
</body>
</html>