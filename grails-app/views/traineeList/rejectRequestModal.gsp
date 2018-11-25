<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="60%"
                         name="rejectApplicantForm"
                         controller="traineeList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeApplicantToRejected" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${traineeList?.encodedId}"></el:hiddenField>

    <msg:modal/>
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

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            name="trainingRejectionReason"
            class=" isRequired"
            size="6"
            controller="trainingRejectionReason"
            action="autocomplete"
            value=""
            label="${message(code: 'traineeList.trainingRejectionReason.label')}"/>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'trainingListEmployeeNote.label')}</h4> <hr/></div>
    <el:formGroup>
        <el:hiddenField name="save_traineeListEmployeeId"/>

        <el:textField name="orderNo" size="6" class=""
                      label="${message(code: 'trainingListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
        <el:dateField name="noteDate" size="6" class=" isRequired"
                      label="${message(code: 'trainingListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>

    <el:formGroup>
        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'trainingListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:row/>

    <el:hiddenField name="check_applicantTableInTraineeList" value=""/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#check_applicantTableInTraineeList").val(_dataTablesCheckBoxValues['applicantTableInTraineeList']);
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>