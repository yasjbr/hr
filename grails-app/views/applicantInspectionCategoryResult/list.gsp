<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'applicantInspectionCategoryResult.entities', default: 'ApplicantInspectionCategoryResult List')}" />
    <g:set var="entity" value="${message(code: 'applicantInspectionCategoryResult.entity', default: 'ApplicantInspectionCategoryResult')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ApplicantInspectionCategoryResult List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="applicantInspectionCategoryResultCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'applicantInspectionCategoryResult',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="applicantInspectionCategoryResultSearchForm">
            <g:render template="/applicantInspectionCategoryResult/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['applicantInspectionCategoryResultTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('applicantInspectionCategoryResultSearchForm');_dataTables['applicantInspectionCategoryResultTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="applicantInspectionCategoryResultTable" searchFormName="applicantInspectionCategoryResultSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="applicantInspectionCategoryResult" spaceBefore="true" hasRow="true" action="filter" serviceName="applicantInspectionCategoryResult">
    <el:dataTableAction controller="applicantInspectionCategoryResult" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show applicantInspectionCategoryResult')}" />
    <el:dataTableAction controller="applicantInspectionCategoryResult" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit applicantInspectionCategoryResult')}" />
    <el:dataTableAction controller="applicantInspectionCategoryResult" action="delete" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete applicantInspectionCategoryResult')}" />
</el:dataTable>
</body>
</html>