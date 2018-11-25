<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personLanguageInfo.entities', default: 'PersonLanguageInfo List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonLanguageInfo List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personLanguageInfoCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personLanguageInfo',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personLanguageInfoSearchForm">
            <g:render template="/pcore/person/personLanguageInfo/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personLanguageInfoTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personLanguageInfoSearchForm');_dataTables['personLanguageInfoTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/personLanguageInfo/dataTable" model="[title:title]"/>
</body>
</html>