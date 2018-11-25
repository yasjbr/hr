<el:modal isModalWithDiv="true"  id="previousJudgmentModal" title="${message(code:'disciplinaryRequest.previousViolationsAndJudgments.label')}" preventCloseOutSide="true" width="80%">
    <el:form action="#" name="disciplinaryRequestSearchForm" style="display: none;">
        <el:hiddenField name="employee.id" value="${employeeId}"/>
        <el:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}"/>
    </el:form>
    <el:dataTable id="disciplinaryRequestTable" searchFormName="disciplinaryRequestSearchForm"
                  hasCheckbox="false" widthClass="col-sm-12" controller="disciplinaryRecordJudgment"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="disciplinaryRecordJudgment" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>
</el:modal>

 <script type="text/javascript">
     gui.dataTable.initialize();
 </script>