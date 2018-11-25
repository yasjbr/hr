<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'allowanceListEmployeeNote.entities', default: 'AllowanceListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'allowanceListEmployeeNote.entity', default: 'AllowanceListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'AllowanceListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="allowanceListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'allowanceListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="allowanceListEmployeeNoteSearchForm">
            <g:render template="/allowanceListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['allowanceListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('allowanceListEmployeeNoteSearchForm');_dataTables['allowanceListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="allowanceListEmployeeNoteTable" searchFormName="allowanceListEmployeeNoteSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="allowanceListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="allowanceListEmployeeNote">
    <el:dataTableAction controller="allowanceListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show allowanceListEmployeeNote')}" />
    <el:dataTableAction controller="allowanceListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete allowanceListEmployeeNote')}" />
</el:dataTable>
</body>
</html>