<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'maritalStatusEmployeeNote.entities', default: 'MaritalStatusEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'maritalStatusEmployeeNote.entity', default: 'MaritalStatusEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'MaritalStatusEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="maritalStatusEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'maritalStatusEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="maritalStatusEmployeeNoteSearchForm">
            <g:render template="/maritalStatusEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['maritalStatusEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('maritalStatusEmployeeNoteSearchForm');_dataTables['maritalStatusEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="maritalStatusEmployeeNoteTable" searchFormName="maritalStatusEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="maritalStatusEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="maritalStatusEmployeeNote">
    <el:dataTableAction controller="maritalStatusEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show maritalStatusEmployeeNote')}" />
    <el:dataTableAction controller="maritalStatusEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit maritalStatusEmployeeNote')}" />
    <el:dataTableAction controller="maritalStatusEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete maritalStatusEmployeeNote')}" />
</el:dataTable>
</body>
</html>