<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'disciplinaryReason.entities', default: 'DisciplinaryReason List')}" />
    <g:set var="entity" value="${message(code: 'disciplinaryReason.entity', default: 'DisciplinaryReason')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'DisciplinaryReason List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="disciplinaryReasonCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'disciplinaryReason',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="disciplinaryReasonSearchForm">
            <g:render template="/disciplinaryReason/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['disciplinaryReasonTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('disciplinaryReasonSearchForm');_dataTables['disciplinaryReasonTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="disciplinaryReasonTable" searchFormName="disciplinaryReasonSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="disciplinaryReason" spaceBefore="true" hasRow="true" action="filter" serviceName="disciplinaryReason">
    <el:dataTableAction controller="disciplinaryReason" actionParams="encodedId" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show disciplinaryReason')}" />
    <el:dataTableAction controller="disciplinaryReason" actionParams="encodedId" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit disciplinaryReason')}" />
    <el:dataTableAction controller="disciplinaryReason" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete disciplinaryReason')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>