<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'applicantInspectionResultListEmployeeNote.entities', default: 'ApplicantInspectionResultListEmployeeNote List')}" />
    <g:set var="entity" value="${message(code: 'applicantInspectionResultListEmployeeNote.entity', default: 'ApplicantInspectionResultListEmployeeNote')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ApplicantInspectionResultListEmployeeNote List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="applicantInspectionResultListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'applicantInspectionResultListEmployeeNote',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="applicantInspectionResultListEmployeeNoteSearchForm">
            <g:render template="/applicantInspectionResultListEmployeeNote/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['applicantInspectionResultListEmployeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('applicantInspectionResultListEmployeeNoteSearchForm');_dataTables['applicantInspectionResultListEmployeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="applicantInspectionResultListEmployeeNoteTable" searchFormName="applicantInspectionResultListEmployeeNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="applicantInspectionResultListEmployeeNote" spaceBefore="true" hasRow="true" action="filter" serviceName="applicantInspectionResultListEmployeeNote">
    <el:dataTableAction controller="applicantInspectionResultListEmployeeNote" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show applicantInspectionResultListEmployeeNote')}" />
    <el:dataTableAction controller="applicantInspectionResultListEmployeeNote" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit applicantInspectionResultListEmployeeNote')}" />
    <el:dataTableAction controller="applicantInspectionResultListEmployeeNote" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete applicantInspectionResultListEmployeeNote')}" />
</el:dataTable>
</body>
</html>