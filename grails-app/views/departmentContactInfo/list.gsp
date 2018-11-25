<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'departmentContactInfo.entities', default: 'DepartmentContactInfo List')}" />
    <g:set var="entity" value="${message(code: 'departmentContactInfo.entity', default: 'DepartmentContactInfo')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'DepartmentContactInfo List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="departmentContactInfoCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'departmentContactInfo',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="departmentContactInfoSearchForm">
            <g:render template="/departmentContactInfo/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['departmentContactInfoTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('departmentContactInfoSearchForm');_dataTables['departmentContactInfoTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="dataTable" model="[title:title]"/>
</body>
</html>