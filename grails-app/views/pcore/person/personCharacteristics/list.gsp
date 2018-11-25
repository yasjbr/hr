<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personCharacteristics.entities', default: 'PersonCharacteristics List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonCharacteristics List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personCharacteristicsCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personCharacteristics',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personCharacteristicsSearchForm">
            <g:render template="/pcore/person/personCharacteristics/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personCharacteristicsTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personCharacteristicsSearchForm');_dataTables['personCharacteristicsTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<g:render template="/pcore/person/personCharacteristics/dataTable" model="[title:title]"/>

</body>
</html>