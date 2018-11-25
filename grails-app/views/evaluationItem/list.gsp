<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'evaluationItem.entities', default: 'EvaluationItem List')}" />
    <g:set var="entity" value="${message(code: 'evaluationItem.entity', default: 'EvaluationItem')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EvaluationItem List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="evaluationItemCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'evaluationItem',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="evaluationItemSearchForm">
            <g:render template="/evaluationItem/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['evaluationItemTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('evaluationItemSearchForm');_dataTables['evaluationItemTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="evaluationItemTable" searchFormName="evaluationItemSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="evaluationItem" spaceBefore="true" hasRow="true" action="filter" serviceName="evaluationItem">
    <el:dataTableAction controller="evaluationItem" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show evaluationItem')}" />
    <el:dataTableAction controller="evaluationItem" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit evaluationItem')}" />
    <el:dataTableAction controller="evaluationItem" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete evaluationItem')}" />
</el:dataTable>
</body>
</html>