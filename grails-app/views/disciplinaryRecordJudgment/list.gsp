<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'disciplinaryRecordJudgment.entities', default: 'DisciplinaryRecordJudgment List')}" />
    <g:set var="entity" value="${message(code: 'disciplinaryRecordJudgment.entity', default: 'DisciplinaryRecordJudgment')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'DisciplinaryRecordJudgment List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="disciplinaryRecordJudgmentCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'disciplinaryRecordJudgment',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="disciplinaryRecordJudgmentSearchForm">
            <g:render template="/disciplinaryRecordJudgment/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['disciplinaryRecordJudgmentTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('disciplinaryRecordJudgmentSearchForm');_dataTables['disciplinaryRecordJudgmentTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="disciplinaryRecordJudgmentTable" searchFormName="disciplinaryRecordJudgmentSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="disciplinaryRecordJudgment" spaceBefore="true" hasRow="true" action="filter" serviceName="disciplinaryRecordJudgment">
    <el:dataTableAction controller="disciplinaryRecordJudgment" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show disciplinaryRecordJudgment')}" />
    <el:dataTableAction controller="disciplinaryRecordJudgment" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit disciplinaryRecordJudgment')}" />
    <el:dataTableAction controller="disciplinaryRecordJudgment" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete disciplinaryRecordJudgment')}" />
</el:dataTable>
</body>
</html>