<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'firmDocument.entities', default: 'FirmDocument List')}" />
    <g:set var="entity" value="${message(code: 'firmDocument.entity', default: 'FirmDocument')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'FirmDocument List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="firmDocumentCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'firmDocument',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="firmDocumentSearchForm">
            <g:render template="/firmDocument/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['firmDocumentTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('firmDocumentSearchForm');_dataTables['firmDocumentTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="firmDocumentTable" searchFormName="firmDocumentSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="firmDocument" spaceBefore="true" hasRow="true" action="filter" serviceName="firmDocument">
    <el:dataTableAction controller="firmDocument" action="show" actionParams="encodedId"  class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show firmDocument')}" />
    <el:dataTableAction controller="firmDocument" action="edit" actionParams="encodedId"  class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit firmDocument')}" />
    <el:dataTableAction controller="firmDocument" action="delete" actionParams="encodedId"  class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete firmDocument')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>