<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'recruitmentListEmployee.entities', default: 'RecruitmentListEmployee List')}" />
    <g:set var="entity" value="${message(code: 'recruitmentListEmployee.entity', default: 'RecruitmentListEmployee')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'RecruitmentListEmployee List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="recruitmentListEmployeeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'recruitmentListEmployee',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="recruitmentListEmployeeSearchForm">
            <g:render template="/recruitmentListEmployee/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['recruitmentListEmployeeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('recruitmentListEmployeeSearchForm');_dataTables['recruitmentListEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="recruitmentListEmployeeTable" searchFormName="recruitmentListEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="recruitmentListEmployee" spaceBefore="true" hasRow="true" action="filter" serviceName="recruitmentListEmployee">
    <el:dataTableAction controller="recruitmentListEmployee" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show recruitmentListEmployee')}" />
    <el:dataTableAction controller="recruitmentListEmployee" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit recruitmentListEmployee')}" />
    <el:dataTableAction controller="recruitmentListEmployee" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete recruitmentListEmployee')}" />
</el:dataTable>
</body>
</html>