<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'employmentRecord.entities', default: 'EmploymentRecord List')}" />
    <g:set var="entity" value="${message(code: 'employmentRecord.entity', default: 'EmploymentRecord')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EmploymentRecord List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employmentRecordCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employmentRecord',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employmentRecordSearchForm">
            <g:render template="/employmentRecord/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employmentRecordTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employmentRecordSearchForm');_dataTables['employmentRecordTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/employmentRecord/dataTable" model="[title:title]"/>

</body>
</html>