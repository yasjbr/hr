<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="traineeList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeApplicantToTrainingPassed" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${traineeList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <msg:info/>

    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'traineeList.code.label', default: 'code')}"
                      value="${traineeList?.code}"
                      isReadOnly="true"/>

        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'traineeList.name.label', default: 'name')}"
                      value="${traineeList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'traineeList.trackingInfo.dateCreatedUTC.label')}"
                      value="${traineeList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'traineeList.transientData.receiveDate.label')}"
                      value="${traineeList.transientData.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:select
                valueMessagePrefix="EnumTrainingEvaluation"
                from="${ps.gov.epsilon.hr.enums.v1.EnumTrainingEvaluation.values()}"
                name="trainingEvaluation"
                size="6"
                class=" isRequired"
                label="${message(code: 'traineeList.trainingEvaluation.label', default: 'trainingEvaluation')}"/>
        <el:textField name="mark" size="6" class=""
                      label="${message(code: 'traineeList.mark.label', default: 'mark')}"
                      value=""/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'trainingListEmployeeNote.label')}</h4> <hr/></div>

    <el:formGroup>
        <el:textField name="orderNo" size="6" class=""
                      label="${message(code: 'trainingListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
        <el:dateField name="noteDate" size="6" class=""
                      label="${message(code: 'trainingListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'trainingListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:row/>

    <el:hiddenField name="check_applicantTableInTraineeList" value=""/>

    <el:formButton isSubmit="true" functionName="save"/>

    <el:row/>

</el:validatableModalForm>

<script type="text/javascript">

    if (_dataTablesCheckBoxValues['applicantTableInTraineeList'].length > 1) {
        $('.alert-info').html("${g.message(code:'traineeList.approveModal.mark.message',default: 'mark will be add for applicant')}");
    } else {
        $('.alert-info').html("");
        $('.alert-info').hide();
    }


    $("#check_applicantTableInTraineeList").val(_dataTablesCheckBoxValues['applicantTableInTraineeList']);
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>