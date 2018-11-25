<el:modal isModalWithDiv="true"  id="previousAbsenceModal" title="${message(code:'absence.previous.label')}" preventCloseOutSide="true" width="80%">
    <el:form action="#" name="absenceSearchForm" style="display: none;">
        <el:hiddenField name="employee.id" value="${employeeId}"/>
        <el:hiddenField name="idsToExclude[]" value="${absenceId}"/>
    </el:form>
    <el:dataTable id="absenceTable" searchFormName="absenceSearchForm"
                  hasCheckbox="false" widthClass="col-sm-12" controller="absence"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="absence" viewExtendButtons="false" domainColumns="DOMAIN_TAB_COLUMNS">
    </el:dataTable>
</el:modal>

 <script type="text/javascript">
     gui.dataTable.initialize();
 </script>