<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'contactInfo.entities', default: 'ContactInfo List')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ContactInfo List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="contactInfoCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'contactInfo',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="contactInfoSearchForm">
            <g:render template="/pcore/person/contactInfo/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['contactInfoTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('contactInfoSearchForm');_dataTables['contactInfoTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/pcore/person/contactInfo/dataTable" model="[title:title]"/>

</body>
</html>