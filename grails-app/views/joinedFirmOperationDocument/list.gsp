<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'joinedFirmOperationDocument.entities', default: 'JoinedFirmOperationDocument List')}" />
    <g:set var="entity" value="${message(code: 'joinedFirmOperationDocument.entity', default: 'JoinedFirmOperationDocument')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'JoinedFirmOperationDocument List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="joinedFirmOperationDocumentCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'joinedFirmOperationDocument',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="joinedFirmOperationDocumentSearchForm">
            <g:render template="/joinedFirmOperationDocument/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['joinedFirmOperationDocumentTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('joinedFirmOperationDocumentSearchForm');_dataTables['joinedFirmOperationDocumentTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="joinedFirmOperationDocumentTable" searchFormName="joinedFirmOperationDocumentSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="joinedFirmOperationDocument" spaceBefore="true" hasRow="true" action="filter" serviceName="joinedFirmOperationDocument">
    <el:dataTableAction controller="joinedFirmOperationDocument" action="show" actionParams="transientData.operation" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show joinedFirmOperationDocument')}" />
    <el:dataTableAction controller="joinedFirmOperationDocument" action="edit" actionParams="transientData.operation"  class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit joinedFirmOperationDocument')}" />
</el:dataTable>
</body>
</html>