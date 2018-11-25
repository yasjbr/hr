<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'militaryRankClassification.entities', default: 'MilitaryRankClassification List')}" />
    <g:set var="entity" value="${message(code: 'militaryRankClassification.entity', default: 'MilitaryRankClassification')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'MilitaryRankClassification List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="militaryRankClassificationCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'militaryRankClassification',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="militaryRankClassificationSearchForm">
            <g:render template="/militaryRankClassification/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['militaryRankClassificationTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('militaryRankClassificationSearchForm');_dataTables['militaryRankClassificationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="militaryRankClassificationTable" searchFormName="militaryRankClassificationSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="militaryRankClassification" spaceBefore="true" hasRow="true" action="filter" serviceName="militaryRankClassification">
    <el:dataTableAction controller="militaryRankClassification" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show militaryRankClassification')}" />
    <el:dataTableAction controller="militaryRankClassification" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit militaryRankClassification')}" />
    <el:dataTableAction controller="militaryRankClassification" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete militaryRankClassification')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>