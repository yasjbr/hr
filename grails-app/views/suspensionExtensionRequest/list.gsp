<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'suspensionExtensionRequest.entities', default: 'SuspensionExtensionRequest List')}" />
    <g:set var="entity" value="${message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'SuspensionExtensionRequest List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="suspensionExtensionRequestCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'suspensionExtensionRequest',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="suspensionExtensionRequestSearchForm">
            <g:render template="/suspensionExtensionRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['suspensionExtensionRequestTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('suspensionExtensionRequestSearchForm');_dataTables['suspensionExtensionRequestTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="suspensionExtensionRequestTable" searchFormName="suspensionExtensionRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="suspensionExtensionRequest" spaceBefore="true" hasRow="true" action="filter" serviceName="suspensionExtensionRequest">
    <el:dataTableAction controller="suspensionExtensionRequest" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show suspensionExtensionRequest')}" />
    <el:dataTableAction controller="suspensionExtensionRequest" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit suspensionExtensionRequest')}" />
    <el:dataTableAction controller="suspensionExtensionRequest" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete suspensionExtensionRequest')}" />
</el:dataTable>
</body>
</html>