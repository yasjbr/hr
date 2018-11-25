<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'trainingListEmployeeNote.entities', default: 'TrainingListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'trainingListEmployeeNote.entity', default: 'TrainingListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'TrainingListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="trainingListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'trainingListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="trainingListEmployeeNoteSearchForm">
            <g:render template="/trainingListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['trainingListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('trainingListEmployeeNoteSearchForm');_dataTables['trainingListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="trainingListEmployeeNoteTable" searchFormName="trainingListEmployeeNoteSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="trainingListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="trainingListEmployeeNote">
    <el:dataTableAction controller="trainingListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show trainingListEmployeeNote')}" />
    <el:dataTableAction controller="trainingListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit trainingListEmployeeNote')}" />
    <el:dataTableAction controller="trainingListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete trainingListEmployeeNote')}" />
</el:dataTable>
</body>
</html>