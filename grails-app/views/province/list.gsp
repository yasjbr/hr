<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'province.entities', default: 'Province List')}" />
    <g:set var="entity" value="${message(code: 'province.entity', default: 'Province')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Province List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="provinceCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'province',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="provinceSearchForm">
            <g:render template="/province/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['provinceTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('provinceSearchForm');_dataTables['provinceTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="provinceTable" searchFormName="provinceSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="province" spaceBefore="true" hasRow="true" action="filter" serviceName="province">
    <el:dataTableAction controller="province" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show province')}" />
    <el:dataTableAction controller="province" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit province')}" />
    <el:dataTableAction controller="province" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete province')}" />
</el:dataTable>
</body>
</html>