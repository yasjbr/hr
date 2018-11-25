<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personCountryVisit.entities', default: 'PersonCountryVisit List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonCountryVisit List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personCountryVisitCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personCountryVisit',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personCountryVisitSearchForm">
            <g:render template="/pcore/person/personCountryVisit/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personCountryVisitTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personCountryVisitSearchForm');_dataTables['personCountryVisitTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<g:render template="/pcore/person/personCountryVisit/dataTable" model="[title:title]"/>

</body>
</html>