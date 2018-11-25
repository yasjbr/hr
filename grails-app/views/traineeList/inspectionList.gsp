<g:set var="entities"
       value="${message(code: 'applicantInspectionCategoryResult.entities', default: 'applicantInspectionCategoryResult List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'applicantInspectionCategoryResult List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
    <msg:page/>


    <el:dataTable id="employeeNoteTable"
                  searchFormName="applicantInspectionCategoryResultSearchForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="false"
                  widthClass="col-sm-12"
                  controller="applicantInspectionCategoryResult"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="applicantInspectionCategoryResult" domainColumns="DOMAIN_TAB_COLUMNS">
    </el:dataTable>
</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>