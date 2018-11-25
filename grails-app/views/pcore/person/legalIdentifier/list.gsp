<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'legalIdentifier.entities', default: 'LegalIdentifier List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LegalIdentifier List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="legalIdentifierCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'legalIdentifier',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="legalIdentifierSearchForm">
            <g:render template="/pcore/person/legalIdentifier/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['legalIdentifierTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('legalIdentifierSearchForm');_dataTables['legalIdentifierTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/legalIdentifier/dataTable" model="[title:title]"/>
</body>
</html>