<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'serviceActionReasonType.entities', default: 'ServiceActionReasonType List')}" />
    <g:set var="entity" value="${message(code: 'serviceActionReasonType.entity', default: 'ServiceActionReasonType')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ServiceActionReasonType List')}" />
    <title>${title}</title>
</head>
<body>
<script>
    function resetSearchForm(){
        gui.formValidatable.resetForm('serviceActionReasonTypeSearchForm');
        $("#isRelatedToEndOfService").attr("checked",false);
        $("#isRelatedToEndOfService").val(false);
        _dataTables['serviceActionReasonTypeTable'].draw();
    }
</script>
<msg:page />
<lay:collapseWidget id="serviceActionReasonTypeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'serviceActionReasonType',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="serviceActionReasonTypeSearchForm">
            <g:render template="/serviceActionReasonType/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['serviceActionReasonTypeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="resetSearchForm();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="serviceActionReasonTypeTable" searchFormName="serviceActionReasonTypeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="serviceActionReasonType" spaceBefore="true" hasRow="true" action="filter" serviceName="serviceActionReasonType">
    <el:dataTableAction controller="serviceActionReasonType" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show serviceActionReasonType')}" />
    <el:dataTableAction controller="serviceActionReasonType" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit serviceActionReasonType')}" />
    <el:dataTableAction controller="serviceActionReasonType" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete serviceActionReasonType')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>