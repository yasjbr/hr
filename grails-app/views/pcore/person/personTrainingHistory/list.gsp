<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personTrainingHistory.entities', default: 'PersonTrainingHistory List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonTrainingHistory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personTrainingHistoryCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personTrainingHistory',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personTrainingHistorySearchForm">
            <g:render template="/pcore/person/personTrainingHistory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personTrainingHistoryTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personTrainingHistorySearchForm');_dataTables['personTrainingHistoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/personTrainingHistory/dataTable" model="[title:title]"/>

</body>
</html>