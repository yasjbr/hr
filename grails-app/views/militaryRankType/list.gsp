<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'militaryRankType.entities', default: 'MilitaryRankType List')}" />
    <g:set var="entity" value="${message(code: 'militaryRankType.entity', default: 'MilitaryRankType')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'MilitaryRankType List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="militaryRankTypeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'militaryRankType',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="militaryRankTypeSearchForm">
            <g:render template="/militaryRankType/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['militaryRankTypeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('militaryRankTypeSearchForm');_dataTables['militaryRankTypeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="militaryRankTypeTable" searchFormName="militaryRankTypeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="militaryRankType" spaceBefore="true" hasRow="true" action="filter" serviceName="militaryRankType">
    <el:dataTableAction controller="militaryRankType" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show militaryRankType')}" />
    <el:dataTableAction controller="militaryRankType" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit militaryRankType')}" />
    <el:dataTableAction controller="militaryRankType" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete militaryRankType')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>