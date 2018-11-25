<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'externalTransferListEmployee.entities', default: 'ExternalTransferListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'externalTransferListEmployee.entity', default: 'ExternalTransferListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ExternalTransferListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="externalTransferListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'externalTransferListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="externalTransferListEmployeeSearchForm">
            <g:render template="/externalTransferListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['externalTransferListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('externalTransferListEmployeeSearchForm');_dataTables['externalTransferListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="externalTransferListEmployeeTable" searchFormName="externalTransferListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="externalTransferListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="externalTransferListEmployee">
    <el:dataTableAction controller="externalTransferListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show externalTransferListEmployee')}" />
    <el:dataTableAction controller="externalTransferListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit externalTransferListEmployee')}" />
    <el:dataTableAction controller="externalTransferListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete externalTransferListEmployee')}" />
</el:dataTable>
</body>
</html>