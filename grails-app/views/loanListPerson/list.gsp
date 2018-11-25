<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'loanListPerson.entities', default: 'LoanListPerson List')}" />
    <g:set var="entity" value="${message(code: 'loanListPerson.entity', default: 'LoanListPerson')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'LoanListPerson List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="loanListPersonCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'loanListPerson',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="loanListPersonSearchForm">
            <g:render template="/loanListPerson/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['loanListPersonTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanListPersonSearchForm');_dataTables['loanListPersonTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="loanListPersonTable" searchFormName="loanListPersonSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanListPerson" spaceBefore="true" hasRow="true" action="filter" serviceName="loanListPerson">
    <el:dataTableAction controller="loanListPerson" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show loanListPerson')}" />
    <el:dataTableAction controller="loanListPerson" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit loanListPerson')}" />
    <el:dataTableAction controller="loanListPerson" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete loanListPerson')}" />
</el:dataTable>
</body>
</html>