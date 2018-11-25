<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personEmploymentHistory.entities', default: 'PersonEmploymentHistory List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonEmploymentHistory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personEmploymentHistoryCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personEmploymentHistory',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personEmploymentHistorySearchForm">
            <g:render template="/pcore/person/personEmploymentHistory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personEmploymentHistoryTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personEmploymentHistorySearchForm');_dataTables['personEmploymentHistoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/personEmploymentHistory/dataTable" model="[title:title]"/>

</body>
</html>