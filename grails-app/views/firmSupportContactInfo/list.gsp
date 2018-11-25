<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'firmSupportContactInfo.entities', default: 'FirmSupportContactInfo List')}" />
    <g:set var="entity" value="${message(code: 'firmSupportContactInfo.entity', default: 'FirmSupportContactInfo')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'FirmSupportContactInfo List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="firmSupportContactInfoCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'firmSupportContactInfo',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="firmSupportContactInfoSearchForm">
            <g:render template="/firmSupportContactInfo/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['firmSupportContactInfoTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('firmSupportContactInfoSearchForm');_dataTables['firmSupportContactInfoTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="firmSupportContactInfoTable" searchFormName="firmSupportContactInfoSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="firmSupportContactInfo" spaceBefore="true" hasRow="true" action="filter" serviceName="firmSupportContactInfo">
    <el:dataTableAction controller="firmSupportContactInfo" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show firmSupportContactInfo')}" />
    <el:dataTableAction controller="firmSupportContactInfo" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit firmSupportContactInfo')}" />
    <el:dataTableAction controller="firmSupportContactInfo" action="delete" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete firmSupportContactInfo')}" />
</el:dataTable>
</body>
</html>