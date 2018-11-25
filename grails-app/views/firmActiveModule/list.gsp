<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'firmActiveModule.entities', default: 'FirmActiveModule List')}" />
    <g:set var="entity" value="${message(code: 'firmActiveModule.entity', default: 'FirmActiveModule')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'FirmActiveModule List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="firmActiveModuleCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'firmActiveModule',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="firmActiveModuleSearchForm">
            <g:render template="/firmActiveModule/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['firmActiveModuleTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('firmActiveModuleSearchForm');_dataTables['firmActiveModuleTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="firmActiveModuleTable" searchFormName="firmActiveModuleSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="firmActiveModule" spaceBefore="true" hasRow="true" action="filter" serviceName="firmActiveModule">
    <el:dataTableAction controller="firmActiveModule" action="delete"  actionParams="encodedId"  class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete firmActiveModule')}" />
</el:dataTable>
</body>
</html>