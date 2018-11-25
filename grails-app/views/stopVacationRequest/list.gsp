<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'stopVacationRequest.entities', default: 'StopVacation List')}" />
    <g:set var="entity" value="${message(code: 'stopVacationRequest.entity', default: 'StopVacation')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'StopVacation List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="stopVacationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'stopVacationRequest',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="stopVacationSearchForm">
            <g:render template="/stopVacationRequest/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['stopVacationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('stopVacationSearchForm');_dataTables['stopVacationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="stopVacationTable" searchFormName="stopVacationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="stopVacationRequest" spaceBefore="true"
              hasRow="true" action="filter" serviceName="stopVacationRequest" domainColumns="DOMAIN_COLUMNS">
    <el:dataTableAction controller="stopVacationRequest" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show stopVacationRequest')}" />
    <el:dataTableAction controller="stopVacationRequest" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit stopVacationRequest')}" />
    <el:dataTableAction controller="stopVacationRequest" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete stopVacationRequest')}" />
</el:dataTable>
</body>
</html>