<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'provinceLocation.entities', default: 'ProvinceLocation List')}" />
    <g:set var="entity" value="${message(code: 'provinceLocation.entity', default: 'ProvinceLocation')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ProvinceLocation List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="provinceLocationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'provinceLocation',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="provinceLocationSearchForm">
            <g:render template="/provinceLocation/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['provinceLocationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('provinceLocationSearchForm');_dataTables['provinceLocationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="provinceLocationTable" searchFormName="provinceLocationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="provinceLocation" spaceBefore="true" hasRow="true" action="filter" serviceName="provinceLocation">
    <el:dataTableAction controller="provinceLocation" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show provinceLocation')}" />
    <el:dataTableAction controller="provinceLocation" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit provinceLocation')}" />
    <el:dataTableAction controller="provinceLocation" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete provinceLocation')}" />
</el:dataTable>
</body>
</html>