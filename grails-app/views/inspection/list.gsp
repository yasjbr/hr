<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'inspection.entities', default: 'Inspection List')}" />
    <g:set var="entity" value="${message(code: 'inspection.entity', default: 'Inspection')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Inspection List')}" />
    <title>${title}</title>

    <script>

        function clearSearchForm() {
            $("#test").remove()
            var input = document.createElement("input");
            input.setAttribute("type", "hidden");
            input.setAttribute("id", "test")
            input.setAttribute("name", "allInspection");
            input.setAttribute("value", "false");
            gui.formValidatable.resetForm('inspectionSearchForm')
            _dataTables['inspectionTable'].draw()
        }

        function searchForm(){
            $("#test").remove()
            var input = document.createElement("input");
            input.setAttribute("type", "hidden");
            input.setAttribute("id", "test")
            input.setAttribute("name", "allInspection");
            input.setAttribute("value", "true");
            document.getElementById("inspectionSearchForm").appendChild(input);
            _dataTables['inspectionTable'].draw()
        }
    </script>
</head>
<body>
<msg:page />
<lay:collapseWidget id="inspectionCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'inspection',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="inspectionSearchForm">
            <g:render template="/inspection/search" model="[:]"/>
            <el:formButton functionName="search" onClick="searchForm();"/>
            <el:formButton functionName="clear" onClick="clearSearchForm();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="inspectionTable" searchFormName="inspectionSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="inspection" spaceBefore="true" hasRow="true" action="filter" serviceName="inspection">
    <el:dataTableAction controller="inspection" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show inspection')}" />
    <el:dataTableAction controller="inspection" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit inspection')}" />
    <el:dataTableAction controller="inspection" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete inspection')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>