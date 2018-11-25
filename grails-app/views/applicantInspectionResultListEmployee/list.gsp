<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'applicantInspectionResultListEmployee.entities', default: 'ApplicantInspectionResultListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'applicantInspectionResultListEmployee.entity', default: 'ApplicantInspectionResultListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'ApplicantInspectionResultListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="applicantInspectionResultListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'applicantInspectionResultListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="applicantInspectionResultListEmployeeSearchForm">
            <g:render template="/applicantInspectionResultListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['applicantInspectionResultListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('applicantInspectionResultListEmployeeSearchForm');_dataTables['applicantInspectionResultListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="applicantInspectionResultListEmployeeTable" searchFormName="applicantInspectionResultListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="applicantInspectionResultListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="applicantInspectionResultListEmployee">
    <el:dataTableAction controller="applicantInspectionResultListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show applicantInspectionResultListEmployee')}" />
    <el:dataTableAction controller="applicantInspectionResultListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit applicantInspectionResultListEmployee')}" />
    <el:dataTableAction controller="applicantInspectionResultListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete applicantInspectionResultListEmployee')}" />
</el:dataTable>
</body>
</html>