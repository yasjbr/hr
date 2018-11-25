<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'disciplinaryListJudgmentSetup.entities', default: 'DisciplinaryListJudgmentSetup List')}" />
    <g:set var="entity" value="${message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'DisciplinaryListJudgmentSetup List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="disciplinaryListJudgmentSetupCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'disciplinaryListJudgmentSetup',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="disciplinaryListJudgmentSetupSearchForm">
            <g:render template="/disciplinaryListJudgmentSetup/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['disciplinaryListJudgmentSetupTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('disciplinaryListJudgmentSetupSearchForm');_dataTables['disciplinaryListJudgmentSetupTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="disciplinaryListJudgmentSetupTable" searchFormName="disciplinaryListJudgmentSetupSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="disciplinaryListJudgmentSetup" spaceBefore="true" hasRow="true" action="filter" serviceName="disciplinaryListJudgmentSetup">
    <el:dataTableAction controller="disciplinaryListJudgmentSetup" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show disciplinaryListJudgmentSetup')}" />
    <el:dataTableAction controller="disciplinaryListJudgmentSetup" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit disciplinaryListJudgmentSetup')}" />
    <el:dataTableAction controller="disciplinaryListJudgmentSetup" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete disciplinaryListJudgmentSetup')}" />
</el:dataTable>
</body>
</html>