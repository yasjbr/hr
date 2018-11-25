<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personLiveStatus.entities', default: 'PersonLiveStatus List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonLiveStatus List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personLiveStatusCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personLiveStatus',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personLiveStatusSearchForm">
            <g:render template="/pcore/person/personLiveStatus/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personLiveStatusTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personLiveStatusSearchForm');_dataTables['personLiveStatusTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<g:render template="/pcore/person/personLiveStatus/dataTable" model="[title:title]"/>

</body>
</html>