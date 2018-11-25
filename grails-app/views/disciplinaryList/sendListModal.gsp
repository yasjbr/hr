<el:validatableModalForm title="${message(code:'list.sendList.label')}"
                         width="70%"
                         name="sendListForm"
                         controller="disciplinaryList"
                         hideCancel="true"
                         action="sendList" callBackFunction="callBackFunction" >


    <el:hiddenField name="encodedId" id="encodedId" value="${disciplinaryList?.encodedId}" />

    <msg:modal />

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'disciplinaryList.code.label', default: 'code')}"
                      value="${disciplinaryList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'disciplinaryList.name.label', default: 'name')}"
                      value="${disciplinaryList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'disciplinaryList.trackingInfo.dateCreatedUTC.label')}"
                      value="${disciplinaryList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'disciplinaryList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${disciplinaryList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'disciplinaryList.fromDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="manualOutgoingNo"
                      size="8"
                      class="isRequired"
                      label="${message(code: 'disciplinaryList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"
                      value=""/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>


</el:validatableModalForm>


<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['disciplinaryRecordJudgmentTable'].draw();
            $('#application-modal-main-content').modal("hide");
            $('#sendListButton').remove();
            $('#addDisciplinaryRecordJudgmentButton').remove();
        }
    }
</script>