<el:modal isModalWithDiv="true" id="previousSuspensionModal"
          title="${message(code: 'suspensionRequest.previous.suspension.request.label')}" preventCloseOutSide="true"
          width="80%">

    <el:form action="#" name="suspensionRequestSearchForm" style="display: none;">
        <el:hiddenField name="employee.id" value="${employeeId}"/>
    </el:form>


    <el:dataTable id="suspensionRequestTable" searchFormName="suspensionRequestSearchForm"
                  hasCheckbox="false" widthClass="col-sm-12" controller="suspensionRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="suspensionRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize();
</script>