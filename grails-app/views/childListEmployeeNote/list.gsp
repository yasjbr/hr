<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'childListEmployeeNote.entities', default: 'ChildListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'childListEmployeeNote.entity', default: 'ChildListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ChildListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="childListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'childListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="childListEmployeeNoteSearchForm">
            <g:render template="/childListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['childListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('childListEmployeeNoteSearchForm');_dataTables['childListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="childListEmployeeNoteTable" searchFormName="childListEmployeeNoteSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="childListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="childListEmployeeNote">
    <el:dataTableAction controller="childListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show childListEmployeeNote')}" />
    <el:dataTableAction controller="childListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit childListEmployeeNote')}" />
    <el:dataTableAction controller="childListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete childListEmployeeNote')}" />
</el:dataTable>
</body>
</html>