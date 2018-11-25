<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'employeeSalaryInfo.entities', default: 'EmployeeSalaryInfo List')}"/>
    <g:set var="entity" value="${message(code: 'employeeSalaryInfo.entity', default: 'EmployeeSalaryInfo')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'EmployeeSalaryInfo List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="employeeSalaryInfoCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="employeeSalaryInfoSearchForm">
            <el:hiddenField name="active" value="true"/>
            <g:render template="/employeeSalaryInfo/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeSalaryInfoTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('employeeSalaryInfoSearchForm');_dataTables['employeeSalaryInfoTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<br/>
<br/>
<el:row/>
<el:row/>
<el:validatableResetForm name="importFinancialData" controller="employeeSalaryInfo" action="importFinancialData" callBackFunction="drawDataTable">
    <el:fileInput label="${message(code: 'employeeSalaryInfo.importData.label', default: 'importData')}" id="excelFile"
                  name="excelFile" size="4" class=" isRequired"/>

    <el:dateField label="${message(code: 'employeeSalaryInfo.salaryDate.label', default: 'importData')}" name="salaryDate"
                  id="salaryDate" size="4" value="" class=" isRequired" />


    <btn:button messageCode="upload" class=" btn btn-sm btn-purple btn-primary " icon="ace-icon icon-upload"
                    message="${message(code: 'aocCorrespondenceList.upload.label', default: 'upload')}" isSubmit="true"/>

    <btn:button messageCode="download" class=" btn btn-sm btn-primary" icon="ace-icon icon-export"
                message="${message(code: 'aocCorrespondenceList.download.label', default: 'download')}"
                onclick="window.location.href='${resource(dir: 'extraFiles', file: 'financialInfo.xls')}'"/>
</el:validatableResetForm>
<el:row/>

<el:row/>
<el:dataTable id="employeeSalaryInfoTable" searchFormName="employeeSalaryInfoSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="employeeSalaryInfo" spaceBefore="true" hasRow="true"
              action="filter" serviceName="employeeSalaryInfo">

    <el:dataTableAction controller="employeeSalaryInfo" action="show" actionParams="encodedId" class="green icon-eye"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show employeeSalaryInfo')}"/>
</el:dataTable>

<script>
    function drawDataTable() {
        _dataTables['employeeSalaryInfoTable'].draw();
    }
</script>
</body>
</html>