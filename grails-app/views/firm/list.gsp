<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'firm.entities', default: 'Firm List')}" />
    <g:set var="entity" value="${message(code: 'firm.entity', default: 'Firm')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Firm List')}" />
    <title>${title}</title>
</head>
<body>


<msg:page />
<lay:collapseWidget id="firmCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'firm',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="firmSearchForm">
            <g:render template="/firm/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['firmTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('firmSearchForm');_dataTables['firmTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="firmTable" searchFormName="firmSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="firm" spaceBefore="true" hasRow="true" action="filter" serviceName="firm">
    <el:dataTableAction controller="firm" action="show" actionParams="encodedId"  class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show firm')}" />
    <el:dataTableAction controller="firm" action="edit" actionParams="encodedId"  class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit firm')}" />
%{--
    <el:dataTableAction controller="firm" action="delete" actionParams="encodedId"  class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete firm')}" />
--}%
</el:dataTable>
</body>
</html>