<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'firmSetting.entities', default: 'FirmSetting List')}" />
    <g:set var="entity" value="${message(code: 'firmSetting.entity', default: 'FirmSetting')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'FirmSetting List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="firmSettingCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'firmSetting',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="firmSettingSearchForm">
            <g:render template="/firmSetting/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['firmSettingTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('firmSettingSearchForm');_dataTables['firmSettingTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="firmSettingTable" searchFormName="firmSettingSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="firmSetting" spaceBefore="true" hasRow="true" action="filter" serviceName="firmSetting">
    <el:dataTableAction controller="firmSetting" action="show"  actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show firmSetting')}" />
    <el:dataTableAction controller="firmSetting" action="edit"  actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit firmSetting')}" />
</el:dataTable>
</body>
</html>