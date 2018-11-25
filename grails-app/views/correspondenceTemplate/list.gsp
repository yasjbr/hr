<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'correspondenceTemplate.entities', default: 'CorrespondenceTemplate List')}" />
    <g:set var="entity" value="${message(code: 'correspondenceTemplate.entity', default: 'CorrespondenceTemplate')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'CorrespondenceTemplate List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="correspondenceTemplateCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'correspondenceTemplate',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="correspondenceTemplateSearchForm">
            <g:render template="/correspondenceTemplate/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['correspondenceTemplateTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('correspondenceTemplateSearchForm');_dataTables['correspondenceTemplateTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<g:render template="/correspondenceTemplate/dataTable" model="[title:title]" />

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>