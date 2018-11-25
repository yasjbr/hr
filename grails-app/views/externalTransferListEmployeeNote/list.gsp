<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'externalTransferListEmployeeNote.entities', default: 'ExternalTransferListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'externalTransferListEmployeeNote.entity', default: 'ExternalTransferListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ExternalTransferListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="externalTransferListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'externalTransferListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="externalTransferListEmployeeNoteSearchForm">
            <g:render template="/externalTransferListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['externalTransferListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('externalTransferListEmployeeNoteSearchForm');_dataTables['externalTransferListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="externalTransferListEmployeeNoteTable" searchFormName="externalTransferListEmployeeNoteSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="externalTransferListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="externalTransferListEmployeeNote">
    <el:dataTableAction controller="externalTransferListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show externalTransferListEmployeeNote')}" />
    <el:dataTableAction controller="externalTransferListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit externalTransferListEmployeeNote')}" />
    <el:dataTableAction controller="externalTransferListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete externalTransferListEmployeeNote')}" />
</el:dataTable>
</body>
</html>