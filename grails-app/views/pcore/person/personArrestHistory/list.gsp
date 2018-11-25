<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personArrestHistory.entities', default: 'PersonArrestHistory List')}" />
    <g:set var="entity" value="${message(code: 'personArrestHistory.entity', default: 'PersonArrestHistory')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonArrestHistory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personArrestHistoryCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personArrestHistory',action:'preCreate')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personArrestHistorySearchForm">
            <g:render template="/pcore/person/personArrestHistory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personArrestHistoryTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personArrestHistorySearchForm');_dataTables['personArrestHistoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/personArrestHistory/dataTable" model="[title:title]"/>

</body>
</html>