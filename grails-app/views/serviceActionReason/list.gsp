<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'serviceActionReason.entities', default: 'ServiceActionReason List')}" />
    <g:set var="entity" value="${message(code: 'serviceActionReason.entity', default: 'ServiceActionReason')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ServiceActionReason List')}" />
    <title>${title}</title>
    <script>
        function resetSearchForm(){
            gui.formValidatable.resetForm('serviceActionReasonSearchForm');
            $("#allowReturnToService_").attr("checked",false);
            $("#allowReturnToService").val(false);
            _dataTables['serviceActionReasonTable'].draw();
        }
    </script>
</head>
<body>
<msg:page />
<lay:collapseWidget id="serviceActionReasonCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'serviceActionReason',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="serviceActionReasonSearchForm">
            <g:render template="/serviceActionReason/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['serviceActionReasonTable'].draw()"/>
            <el:formButton functionName="clear" onClick="resetSearchForm();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="serviceActionReasonTable" searchFormName="serviceActionReasonSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="serviceActionReason" spaceBefore="true" hasRow="true" action="filter" serviceName="serviceActionReason">
    <el:dataTableAction controller="serviceActionReason" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show serviceActionReason')}" />
    <el:dataTableAction controller="serviceActionReason" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit serviceActionReason')}" />
    <el:dataTableAction controller="serviceActionReason" action="delete" actionParams="encodedId"
                        showFunction="manageDeleteActions"
                        class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete serviceActionReason')}" />
</el:dataTable>


<script>
    function manageDeleteActions(row) {
        return(row.id != "${ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceActionReason.RETIREMENT.toString()}" &&
               row.id != "${ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceActionReason.SUSPENSION.toString()}" &&
                row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}"
        );
    }
</script>

</body>
</html>