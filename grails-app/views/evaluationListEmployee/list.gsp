<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'evaluationListEmployee.entities', default: 'EvaluationListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'evaluationListEmployee.entity', default: 'EvaluationListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EvaluationListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="evaluationListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'evaluationListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="evaluationListEmployeeSearchForm">
            <g:render template="/evaluationListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['evaluationListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('evaluationListEmployeeSearchForm');_dataTables['evaluationListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="evaluationListEmployeeTable" searchFormName="evaluationListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="evaluationListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="evaluationListEmployee">
    <el:dataTableAction controller="evaluationListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show evaluationListEmployee')}" />
    <el:dataTableAction controller="evaluationListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit evaluationListEmployee')}" />
    <el:dataTableAction controller="evaluationListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete evaluationListEmployee')}" />
</el:dataTable>
</body>
</html>