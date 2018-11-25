<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personMaritalStatus.entities', default: 'PersonMaritalStatus List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonMaritalStatus List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personMaritalStatusCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personMaritalStatus',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personMaritalStatusSearchForm">
            <g:render template="/pcore/person/personMaritalStatus/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personMaritalStatusTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personMaritalStatusSearchForm');_dataTables['personMaritalStatusTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/personMaritalStatus/dataTable" model="[title:title]"/>
</body>
</html>