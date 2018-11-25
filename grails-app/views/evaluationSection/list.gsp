<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'evaluationSection.entities', default: 'EvaluationSection List')}" />
    <g:set var="entity" value="${message(code: 'evaluationSection.entity', default: 'EvaluationSection')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EvaluationSection List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="evaluationSectionCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'evaluationSection',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="evaluationSectionSearchForm">
            <g:render template="/evaluationSection/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['evaluationSectionTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('evaluationSectionSearchForm');_dataTables['evaluationSectionTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="evaluationSectionTable" searchFormName="evaluationSectionSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="evaluationSection" spaceBefore="true" hasRow="true" action="filter" serviceName="evaluationSection">
    <el:dataTableAction controller="evaluationSection" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show evaluationSection')}" />
    <el:dataTableAction controller="evaluationSection" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit evaluationSection')}" />
    <el:dataTableAction controller="evaluationSection" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete evaluationSection')}" />
</el:dataTable>
</body>
</html>