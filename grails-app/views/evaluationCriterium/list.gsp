<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'evaluationCriterium.entities', default: 'EvaluationCriterium List')}" />
    <g:set var="entity" value="${message(code: 'evaluationCriterium.entity', default: 'EvaluationCriterium')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EvaluationCriterium List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="evaluationCriteriumCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'evaluationCriterium',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="evaluationCriteriumSearchForm">
            <g:render template="/evaluationCriterium/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['evaluationCriteriumTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('evaluationCriteriumSearchForm');_dataTables['evaluationCriteriumTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="evaluationCriteriumTable" searchFormName="evaluationCriteriumSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="evaluationCriterium" spaceBefore="true" hasRow="true" action="filter" serviceName="evaluationCriterium">
    <el:dataTableAction controller="evaluationCriterium" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show evaluationCriterium')}" />
    <el:dataTableAction controller="evaluationCriterium" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit evaluationCriterium')}" />
    <el:dataTableAction controller="evaluationCriterium" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete evaluationCriterium')}" />
</el:dataTable>
</body>
</html>