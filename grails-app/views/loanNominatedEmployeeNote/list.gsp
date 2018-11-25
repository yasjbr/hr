<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'loanNominatedEmployeeNote.entities', default: 'LoanListPersonNote List')}" />
    <g:set var="entity" value="${message(code: 'loanNominatedEmployeeNote.entity', default: 'LoanListPersonNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LoanListPersonNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="loanNominatedEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'loanNominatedEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="loanNominatedEmployeeNoteSearchForm">
            <g:render template="/loanNominatedEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanNominatedEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanNominatedEmployeeNoteSearchForm');_dataTables['loanNominatedEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="loanNominatedEmployeeNoteTable" searchFormName="loanNominatedEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanNominatedEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="loanNominatedEmployeeNote">
    <el:dataTableAction controller="loanNominatedEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show loanNominatedEmployeeNote')}" />
    <el:dataTableAction controller="loanNominatedEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit loanNominatedEmployeeNote')}" />
    <el:dataTableAction controller="loanNominatedEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete loanNominatedEmployeeNote')}" />
</el:dataTable>
</body>
</html>