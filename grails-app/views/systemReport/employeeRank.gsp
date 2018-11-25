<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'employeeRank.label', default: 'EmployeeRank')}"/>
    <g:set var="title"
           value="${entity}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="employeeRankCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entity])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="employeeRankSearchForm">
            <g:render template="/systemReport/searchEmployeeRank" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeRankTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('employeeRankSearchForm');_dataTables['employeeRankTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable hidePagination="true" id="employeeRankTable" searchFormName="employeeRankSearchForm"
              dataTableTitle="${title}" messagePrefix="employeeRank"
              hasCheckbox="true" widthClass="col-sm-12" controller="systemReport"
              spaceBefore="true" hasRow="true"
              action="employeeRankFilter" serviceName="systemReport">

</el:dataTable>


<systemReport:showStatic withModal="true" withDataTable="employeeRankTable"
                         iconWithText="true" searchFromName="employeeRankSearchForm"
                   title="${message(code:'employeeRank.label')}"
                   reportName="employeeRankReport"
                   domain="systemReport"
                   method="getMilitaryRankData"
                   format="pdf,xls" reportFormPrefix="EmployeeRank" />


</body>
</html>