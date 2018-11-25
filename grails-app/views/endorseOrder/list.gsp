<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'endorseOrder.entities', default: 'EndorseOrder List')}" />
    <g:set var="entity" value="${message(code: 'endorseOrder.entity', default: 'EndorseOrder')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EndorseOrder List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="endorseOrderCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="endorseOrderSearchForm">
            <g:render template="/endorseOrder/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['endorseOrderTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('endorseOrderSearchForm');_dataTables['endorseOrderTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="endorseOrderTable" searchFormName="endorseOrderSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="endorseOrder" spaceBefore="true" hasRow="true" action="filter" serviceName="endorseOrder">
</el:dataTable>
</body>
</html>