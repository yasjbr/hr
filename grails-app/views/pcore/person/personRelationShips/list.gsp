<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personRelationShips.entities', default: 'PersonRelationShips List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonRelationShips List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personRelationShipsCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personRelationShips',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personRelationShipsSearchForm">
            <g:render template="/pcore/person/personRelationShips/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personRelationShipsTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personRelationShipsSearchForm');_dataTables['personRelationShipsTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/personRelationShips/dataTable" model="[title:title]"/>
</body>
</html>