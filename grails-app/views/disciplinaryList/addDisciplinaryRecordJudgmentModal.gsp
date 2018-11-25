<el:modal isModalWithDiv="true" id="addDisciplinaryRecordJudgmentModal"
          title="${message(code: 'disciplinaryList.addRecordJudgment.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="90%">
    <msg:modal/>
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'disciplinaryRecordJudgment.entities', default: 'list')}" />

    <lay:collapseWidget icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="disciplinaryRecordJudgmentForAddSearchForm">
                <g:render template="/disciplinaryRecordJudgment/search" model="[hideStatusSearch:true]"/>
                <el:hiddenField name="judgmentStatus" value="${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus.NEW.toString()}"/>
                <el:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED.toString()}"/>
                <el:hiddenField name="domainColumns" value="DOMAIN_LIST_FOR_ADD_COLUMNS"/>
                <el:formButton functionName="search" onClick="_dataTables['disciplinaryRecordJudgmentForAddTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('disciplinaryRecordJudgmentForAddSearchForm');_dataTables['disciplinaryRecordJudgmentForAddTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="disciplinaryRecordJudgmentForAddTable" searchFormName="disciplinaryRecordJudgmentForAddSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="disciplinaryRecordJudgment"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="disciplinaryRecordJudgment" viewExtendButtons="false" domainColumns="DOMAIN_LIST_FOR_ADD_COLUMNS">


        <el:dataTableAction controller="disciplinaryRecordJudgment" action="showDetails" type="modal-ajax"  actionParams="encodedId" class="green icon-eye"
                            message="${message(code:'disciplinaryRecordJudgment.showDetails.label',default:'show disciplinaryRecordJudgment')}" />


    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="disciplinaryList"
            action="addDisciplinaryRecordJudgment">

        <el:hiddenField name="checked_recordJudgmentIdsList" value=""/>
        <el:hiddenField name="disciplinaryListId" value="${disciplinaryList?.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_recordJudgmentIdsList").val(_dataTablesCheckBoxValues['disciplinaryRecordJudgmentForAddTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['disciplinaryRecordJudgmentTable'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
