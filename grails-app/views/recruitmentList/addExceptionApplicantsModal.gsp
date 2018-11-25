<el:modal isModalWithDiv="true" id="exceptionalApplicantModal"
          title="${message(code: 'recruitmentList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="applicantSearchTitle" value="${message(code: 'applicant.entities', default: 'Request List')}" />

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [applicantSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="exceptionalApplicantTableSearchForm">
                <g:render template="/applicant/search" model="[isExceptionRecruitmentList:true]"/>
                <el:formButton functionName="search" onClick="_dataTables['exceptionalApplicantTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('exceptionalApplicantTableSearchForm');_dataTables['exceptionalApplicantTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="exceptionalApplicantTable"
                  searchFormName="exceptionalApplicantTableSearchForm"
                  dataTableTitle="${title}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="recruitmentList"
                  action="filterApplicantToAddAsExceptional"
                  spaceBefore="true"
                  hasRow="true"
                  viewExtendButtons="false"
                  domainColumns="DOMAIN_TAB_COLUMNS"
                  serviceName="applicant">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addEligibleApplicantToListForm"
            controller="recruitmentList"
            action="addApplicants">
        <el:hiddenField name="checked_applicantIdsList" value=""/>
        <el:hiddenField name="recruitmentListId" value="${recruitmentList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_applicantIdsList").val(_dataTablesCheckBoxValues['exceptionalApplicantTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['applicantTableInRecruitmentList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
