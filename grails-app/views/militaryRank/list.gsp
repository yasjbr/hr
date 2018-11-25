<%@ page import="grails.util.Holders" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'militaryRank.entities', default: 'MilitaryRank List')}" />
    <g:set var="entity" value="${message(code: 'militaryRank.entity', default: 'MilitaryRank')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'MilitaryRank List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="militaryRankCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'militaryRank',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="militaryRankSearchForm">
            <g:render template="/militaryRank/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['militaryRankTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('militaryRankSearchForm');_dataTables['militaryRankTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="militaryRankTable" searchFormName="militaryRankSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="militaryRank" spaceBefore="true" hasRow="true" action="filter" serviceName="militaryRank">
    <el:dataTableAction controller="militaryRank" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show militaryRank')}" />
    <el:dataTableAction controller="militaryRank" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit militaryRank')}" />
    <el:dataTableAction controller="militaryRank" action="delete" actionParams="encodedId"
                        class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete"
                        message="${message(code:'default.delete.label',args:[entity],default:'delete militaryRank')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>