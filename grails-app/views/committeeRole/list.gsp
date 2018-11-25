<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'committeeRole.entities', default: 'CommitteeRole List')}" />
    <g:set var="entity" value="${message(code: 'committeeRole.entity', default: 'CommitteeRole')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'CommitteeRole List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="committeeRoleCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'committeeRole',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="committeeRoleSearchForm">
            <g:render template="/committeeRole/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['committeeRoleTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('committeeRoleSearchForm');_dataTables['committeeRoleTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="committeeRoleTable" searchFormName="committeeRoleSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="committeeRole" spaceBefore="true" hasRow="true" action="filter" serviceName="committeeRole">
    <el:dataTableAction controller="committeeRole" action="show" class="green icon-eye" actionParams="encodedId" message="${message(code:'default.show.label',args:[entity],default:'show committeeRole')}" />
    <el:dataTableAction controller="committeeRole" action="edit" class="blue icon-pencil" actionParams="encodedId" message="${message(code:'default.edit.label',args:[entity],default:'edit committeeRole')}" />
    <el:dataTableAction controller="committeeRole" action="delete" class="red icon-trash" actionParams="encodedId"  type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete committeeRole')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>