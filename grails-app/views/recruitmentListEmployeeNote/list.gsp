<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'recruitmentListEmployeeNote.entities', default: 'RecruitmentListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'recruitmentListEmployeeNote.entity', default: 'RecruitmentListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'RecruitmentListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="recruitmentListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'recruitmentListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="recruitmentListEmployeeNoteSearchForm">
            <g:render template="/recruitmentListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['recruitmentListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('recruitmentListEmployeeNoteSearchForm');_dataTables['recruitmentListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="recruitmentListEmployeeNoteTable" searchFormName="recruitmentListEmployeeNoteSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="recruitmentListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="recruitmentListEmployeeNote">
    <el:dataTableAction controller="recruitmentListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show recruitmentListEmployeeNote')}" />
    <el:dataTableAction controller="recruitmentListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit recruitmentListEmployeeNote')}" />
    <el:dataTableAction controller="recruitmentListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete recruitmentListEmployeeNote')}" />
</el:dataTable>
</body>
</html>