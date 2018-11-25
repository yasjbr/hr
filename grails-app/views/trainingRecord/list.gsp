<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'trainingRecord.entities', default: 'TrainingRecord List')}" />
    <g:set var="entity" value="${message(code: 'trainingRecord.entity', default: 'TrainingRecord')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'TrainingRecord List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="trainingRecordCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'trainingRecord',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="trainingRecordSearchForm">
            <g:render template="/trainingRecord/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['trainingRecordTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('trainingRecordSearchForm');_dataTables['trainingRecordTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/trainingRecord/dataTable" model="[title:title,entity:entity]"/>
</body>
</html>