<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'trainer.entities', default: 'Trainer List')}" />
    <g:set var="entity" value="${message(code: 'trainer.entity', default: 'Trainer')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'Trainer List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="trainerCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'trainer',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="trainerSearchForm">
            <g:render template="/trainer/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['trainerTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('trainerSearchForm');_dataTables['trainerTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="trainerTable" searchFormName="trainerSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="trainer" spaceBefore="true" hasRow="true" action="filter" serviceName="trainer">
    <el:dataTableAction controller="trainer" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show trainer')}" />
    <el:dataTableAction controller="trainer" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit trainer')}" />
    <el:dataTableAction controller="trainer" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete trainer')}" />
</el:dataTable>
</body>
</html>