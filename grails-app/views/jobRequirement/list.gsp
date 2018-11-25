<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'jobRequirement.entities', default: 'JobRequirement List')}" />
    <g:set var="entity" value="${message(code: 'jobRequirement.entity', default: 'JobRequirement')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'JobRequirement List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="jobRequirementCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'jobRequirement',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="jobRequirementSearchForm">
            <g:render template="/jobRequirement/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['jobRequirementTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('jobRequirementSearchForm');_dataTables['jobRequirementTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="jobRequirementTable" searchFormName="jobRequirementSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="jobRequirement" spaceBefore="true" hasRow="true" action="filter" serviceName="jobRequirement">
    <el:dataTableAction controller="jobRequirement" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show jobRequirement')}" />
    <el:dataTableAction controller="jobRequirement" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit jobRequirement')}" />
    <el:dataTableAction controller="jobRequirement" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete jobRequirement')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>