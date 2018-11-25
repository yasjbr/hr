<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'profileNoticeCategory.entities', default: 'ProfileNoticeCategory List')}" />
    <g:set var="entity" value="${message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ProfileNoticeCategory List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="profileNoticeCategoryCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'profileNoticeCategory',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="profileNoticeCategorySearchForm">
            <g:render template="/profileNoticeCategory/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['profileNoticeCategoryTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('profileNoticeCategorySearchForm');_dataTables['profileNoticeCategoryTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="profileNoticeCategoryTable" searchFormName="profileNoticeCategorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="profileNoticeCategory" spaceBefore="true" hasRow="true" action="filter" serviceName="profileNoticeCategory">
    <el:dataTableAction controller="profileNoticeCategory" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show profileNoticeCategory')}" />
    <el:dataTableAction controller="profileNoticeCategory" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit profileNoticeCategory')}" />
    <el:dataTableAction controller="profileNoticeCategory" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete profileNoticeCategory')}" />
</el:dataTable>
</body>
</html>