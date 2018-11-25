<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'secondmentNotice.entities', default: 'SecondmentNotice List')}" />
    <g:set var="entity" value="${message(code: 'secondmentNotice.entity', default: 'SecondmentNotice')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'SecondmentNotice List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="secondmentNoticeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'secondmentNotice',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="secondmentNoticeSearchForm">
            <g:render template="/secondmentNotice/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['secondmentNoticeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('secondmentNoticeSearchForm');_dataTables['secondmentNoticeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="secondmentNoticeTable" searchFormName="secondmentNoticeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="secondmentNotice" spaceBefore="true" hasRow="true" action="filter" serviceName="secondmentNotice">
    <el:dataTableAction controller="secondmentNotice" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show secondmentNotice')}" />
    <el:dataTableAction controller="secondmentNotice" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit secondmentNotice')}" />
    <el:dataTableAction controller="secondmentNotice" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete secondmentNotice')}" />
</el:dataTable>
</body>
</html>