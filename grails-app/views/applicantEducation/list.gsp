<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personEducation.entities', default: 'personEducation List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'personEducation List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personEducationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personEducation',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personEducationSearchForm">
            <g:render template="/pcore/person/personEducation/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personEducationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personEducationSearchForm');_dataTables['personEducationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="dataTable" model="[title:title]"/>
</body>
</html>