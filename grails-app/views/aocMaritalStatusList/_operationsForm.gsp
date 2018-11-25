<form id="requestSearchForm">
    <el:hiddenField id="employeeId" name="employeeId" value="${employeeId}"/>
    <el:hiddenField id="requestCategory" name="requestCategory" value="${requestCategory}"/>
    <el:hiddenField id="firmId" name="firm.id" value="${firmId}"/>
</form>
<g:set var="title" value="${message(code: 'default.list.label', args: [message(code:'maritalStatusRequest.label')], default: 'MaritalStatusRequest List')}"/>
<el:dataTable id="requestsTable" searchFormName="requestSearchForm"
              dataTableTitle="${title}" isSingleSelect="true" domainColumns="${DOMAIN_COLUMNS}"
              hasCheckbox="true" widthClass="col-sm-12" controller="maritalStatusRequest" spaceBefore="true" hasRow="true"
              action="filterCanHaveOperation" serviceName="maritalStatusRequest">
</el:dataTable>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
</script>