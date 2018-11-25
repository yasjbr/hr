<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'attendanceType.entities', default: 'AttendanceType List')}" />
    <g:set var="entity" value="${message(code: 'attendanceType.entity', default: 'AttendanceType')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'AttendanceType List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="attendanceTypeCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'attendanceType',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="attendanceTypeSearchForm">
            <g:render template="/attendanceType/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['attendanceTypeTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('attendanceTypeSearchForm');_dataTables['attendanceTypeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="attendanceTypeTable" searchFormName="attendanceTypeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="attendanceType" spaceBefore="true" hasRow="true" action="filter" serviceName="attendanceType">
    <el:dataTableAction controller="attendanceType" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show attendanceType')}" />
    <el:dataTableAction controller="attendanceType" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit attendanceType')}" />
    <el:dataTableAction controller="attendanceType" action="delete" actionParams="encodedId"  class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete attendanceType')}" />
</el:dataTable>
</body>
</html>