<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'suspensionListEmployeeNote.entities', default: 'SuspensionListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'suspensionListEmployeeNote.entity', default: 'SuspensionListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'SuspensionListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="suspensionListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'suspensionListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="suspensionListEmployeeNoteSearchForm">
            <g:render template="/suspensionListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['suspensionListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('suspensionListEmployeeNoteSearchForm');_dataTables['suspensionListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="suspensionListEmployeeNoteTable" searchFormName="suspensionListEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="suspensionListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="suspensionListEmployeeNote">
    <el:dataTableAction controller="suspensionListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show suspensionListEmployeeNote')}" />
    <el:dataTableAction controller="suspensionListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit suspensionListEmployeeNote')}" />
    <el:dataTableAction controller="suspensionListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete suspensionListEmployeeNote')}" />
</el:dataTable>
</body>
</html>