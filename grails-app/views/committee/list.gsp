<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'committee.entities', default: 'Committee List')}" />
    <g:set var="entity" value="${message(code: 'committee.entity', default: 'Committee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Committee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="committeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'committee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="committeeSearchForm">
            <g:render template="/committee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['committeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('committeeSearchForm');_dataTables['committeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="committeeTable" searchFormName="committeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="committee" spaceBefore="true" hasRow="true" action="filter" serviceName="committee">
    <el:dataTableAction controller="committee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show committee')}" />
    <el:dataTableAction controller="committee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit committee')}" />
    <el:dataTableAction controller="committee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete committee')}" />
</el:dataTable>
</body>
</html>