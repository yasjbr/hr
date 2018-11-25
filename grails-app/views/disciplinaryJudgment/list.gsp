<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'disciplinaryJudgment.entities', default: 'disciplinaryJudgment List')}" />
    <g:set var="entity" value="${message(code: 'disciplinaryJudgment.entity', default: 'disciplinaryJudgment')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'disciplinaryJudgment List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="disciplinaryJudgmentCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'disciplinaryJudgment',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="disciplinaryJudgmentSearchForm">
            <g:render template="/disciplinaryJudgment/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['disciplinaryJudgmentTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('disciplinaryJudgmentSearchForm');_dataTables['disciplinaryJudgmentTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="disciplinaryJudgmentTable" searchFormName="disciplinaryJudgmentSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="disciplinaryJudgment" spaceBefore="true" hasRow="true" action="filter" serviceName="disciplinaryJudgment">
    <el:dataTableAction controller="disciplinaryJudgment" actionParams="encodedId"  action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show disciplinaryJudgment')}" />
    <el:dataTableAction controller="disciplinaryJudgment" actionParams="encodedId" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit disciplinaryJudgment')}" />
    <el:dataTableAction controller="disciplinaryJudgment" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',args:[entity],default:'delete disciplinaryJudgment')}" />
</el:dataTable>

<script>
    function manageExecuteDelete(row) {
        return (row.trackingInfo.createdBy != "${grails.util.Holders.grailsApplication.config?.grails?.applicationName}");
    }
</script>

</body>
</html>