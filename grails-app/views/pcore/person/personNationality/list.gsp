<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'personNationality.entities', default: 'PersonNationality List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'PersonNationality List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="personNationalityCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'personNationality',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="personNationalitySearchForm">
            <g:render template="/pcore/person/personNationality/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['personNationalityTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('personNationalitySearchForm');_dataTables['personNationalityTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/personNationality/dataTable" model="[title:title]"/>
</body>
</html>