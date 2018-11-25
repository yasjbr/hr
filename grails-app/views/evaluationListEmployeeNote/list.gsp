<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'evaluationListEmployeeNote.entities', default: 'EvaluationListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'evaluationListEmployeeNote.entity', default: 'EvaluationListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EvaluationListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="evaluationListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'evaluationListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="evaluationListEmployeeNoteSearchForm">
            <g:render template="/evaluationListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['evaluationListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('evaluationListEmployeeNoteSearchForm');_dataTables['evaluationListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="evaluationListEmployeeNoteTable" searchFormName="evaluationListEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="evaluationListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="evaluationListEmployeeNote">
    <el:dataTableAction controller="evaluationListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show evaluationListEmployeeNote')}" />
    <el:dataTableAction controller="evaluationListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit evaluationListEmployeeNote')}" />
    <el:dataTableAction controller="evaluationListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete evaluationListEmployeeNote')}" />
</el:dataTable>
</body>
</html>