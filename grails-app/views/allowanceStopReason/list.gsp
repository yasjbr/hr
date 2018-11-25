<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'allowanceStopReason.entities', default: 'AllowanceStopReason List')}" />
    <g:set var="entity" value="${message(code: 'allowanceStopReason.entity', default: 'AllowanceStopReason')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'AllowanceStopReason List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="allowanceStopReasonCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'allowanceStopReason',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="allowanceStopReasonSearchForm">
            <g:render template="/allowanceStopReason/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['allowanceStopReasonTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('allowanceStopReasonSearchForm');_dataTables['allowanceStopReasonTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="allowanceStopReasonTable" searchFormName="allowanceStopReasonSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="allowanceStopReason" spaceBefore="true" hasRow="true" action="filter" serviceName="allowanceStopReason">
    <el:dataTableAction controller="allowanceStopReason" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show allowanceStopReason')}" />
    <el:dataTableAction controller="allowanceStopReason" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit allowanceStopReason')}" />
    <el:dataTableAction controller="allowanceStopReason" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete allowanceStopReason')}" />
</el:dataTable>
</body>
</html>