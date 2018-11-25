<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'contactInfo.entities', default: 'applicantContactInfo List')}" />
    <g:set var="entity" value="${message(code: 'contactInfo.entity', default: 'applicantContactInfo')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'applicantContactInfo List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="applicantContactInfoCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'applicantContactInfo',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="applicantContactInfoSearchForm">
            <g:render template="/applicantContactInfo/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['applicantContactInfoTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('applicantContactInfoSearchForm');_dataTables['applicantContactInfoTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="dataTable" model="[title:title]"/>
</body>
</html>