<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'evaluationTemplate.entities', default: 'EvaluationTemplate List')}" />
    <g:set var="entity" value="${message(code: 'evaluationTemplate.entity', default: 'EvaluationTemplate')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EvaluationTemplate List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="evaluationTemplateCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'evaluationTemplate',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="evaluationTemplateSearchForm">
            <g:render template="/evaluationTemplate/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['evaluationTemplateTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('evaluationTemplateSearchForm');_dataTables['evaluationTemplateTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="evaluationTemplateTable" searchFormName="evaluationTemplateSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="evaluationTemplate" spaceBefore="true" hasRow="true" action="filter" serviceName="evaluationTemplate">
    <el:dataTableAction controller="evaluationTemplate" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show evaluationTemplate')}" />
    <el:dataTableAction controller="evaluationTemplate" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit evaluationTemplate')}" />
    <el:dataTableAction controller="evaluationTemplate" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete evaluationTemplate')}" />
</el:dataTable>
</body>
</html>