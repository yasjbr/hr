<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'loanListPersonNote.entities', default: 'LoanListPersonNote List')}" />
    <g:set var="entity" value="${message(code: 'loanListPersonNote.entity', default: 'LoanListPersonNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LoanListPersonNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="loanListPersonNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'loanListPersonNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="loanListPersonNoteSearchForm">
            <g:render template="/loanListPersonNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanListPersonNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanListPersonNoteSearchForm');_dataTables['loanListPersonNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="loanListPersonNoteTable" searchFormName="loanListPersonNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanListPersonNote" spaceBefore="true" hasRow="true" action="filter" serviceName="loanListPersonNote">
    <el:dataTableAction controller="loanListPersonNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show loanListPersonNote')}" />
    <el:dataTableAction controller="loanListPersonNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit loanListPersonNote')}" />
    <el:dataTableAction controller="loanListPersonNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete loanListPersonNote')}" />
</el:dataTable>
</body>
</html>