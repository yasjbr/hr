<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'personHealthHistory.entity', default: 'PersonHealthHistory List')}" />
    <g:set var="entities" value="${message(code: 'personHealthHistory.entities', default: 'PersonHealthHistory List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonHealthHistory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personHealthHistoryCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personHealthHistory',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personHealthHistorySearchForm">
            <g:render template="/pcore/person/personHealthHistory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personHealthHistoryTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personHealthHistorySearchForm');_dataTables['personHealthHistoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/personHealthHistory/dataTable" model="[entity:entity]"/>
</body>
</html>