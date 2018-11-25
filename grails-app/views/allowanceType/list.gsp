<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'allowanceType.entities', default: 'AllowanceType List')}" />
    <g:set var="entity" value="${message(code: 'allowanceType.entity', default: 'AllowanceType')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'AllowanceType List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="allowanceTypeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'allowanceType',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="allowanceTypeSearchForm">
            <g:render template="/allowanceType/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['allowanceTypeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('allowanceTypeSearchForm');_dataTables['allowanceTypeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="allowanceTypeTable" searchFormName="allowanceTypeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="allowanceType" spaceBefore="true" hasRow="true" action="filter" serviceName="allowanceType">
    <el:dataTableAction controller="allowanceType" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show allowanceType')}" />
    <el:dataTableAction controller="allowanceType" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit allowanceType')}" />
    <el:dataTableAction controller="allowanceType" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete allowanceType')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>