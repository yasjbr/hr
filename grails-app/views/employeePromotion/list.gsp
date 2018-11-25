<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'employeePromotion.entities', default: 'EmployeePromotion List')}" />
    <g:set var="entity" value="${message(code: 'employeePromotion.entity', default: 'EmployeePromotion')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'EmployeePromotion List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="employeePromotionCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'employeePromotion',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employeePromotionSearchForm">
            <g:render template="/employeePromotion/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeePromotionTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('employeePromotionSearchForm');_dataTables['employeePromotionTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/employeePromotion/dataTable" model="[title:title]"/>
</body>
</html>