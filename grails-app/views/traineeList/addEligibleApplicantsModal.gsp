<el:modal isModalWithDiv="true" id="eligibleApplicantModal"
          title="${message(code: 'traineeList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="applicantSearchTitle" value="${message(code: 'applicant.entities', default: 'Request List')}" />

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [applicantSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="eligibleApplicantTableSearchForm">
                <g:render template="/applicant/search" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['eligibleApplicantTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('eligibleApplicantTableSearchForm');_dataTables['eligibleApplicantTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="eligibleApplicantTable"
                  searchFormName="eligibleApplicantTableSearchForm"
                  dataTableTitle="${title}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="traineeList"
                  action="filterApplicantToAdd"
                  spaceBefore="true"
                  hasRow="true"
                  viewExtendButtons="false"
                  domainColumns="DOMAIN_TAB_COLUMNS"
                  serviceName="applicant">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addEligibleApplicantToListForm"
            controller="traineeList"
            action="addApplicants">
        <el:hiddenField name="checked_applicantIdsList" value=""/>
        <el:hiddenField name="traineeListId" value="${traineeList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_applicantIdsList").val(_dataTablesCheckBoxValues['eligibleApplicantTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['applicantTableInTraineeList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
