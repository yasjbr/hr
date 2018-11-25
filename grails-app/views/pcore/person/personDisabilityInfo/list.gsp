<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personDisabilityInfo.entities', default: 'PersonDisabilityInfo List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonDisabilityInfo List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personDisabilityInfoCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personDisabilityInfo',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personDisabilityInfoSearchForm">
            <g:render template="/pcore/person/personDisabilityInfo/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personDisabilityInfoTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personDisabilityInfoSearchForm');_dataTables['personDisabilityInfoTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<g:render template="/pcore/person/personDisabilityInfo/dataTable" model="[title:title]"/>

</body>
</html>