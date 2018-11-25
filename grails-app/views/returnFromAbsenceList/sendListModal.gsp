<el:validatableModalForm title="${message(code: 'list.sendList.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="returnFromAbsenceList"
                         hideCancel="true"
                         action="sendList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${returnFromAbsenceList?.encodedId}"></el:hiddenField>
    <msg:modal/>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.code.label', default: 'code')}"
                      value="${returnFromAbsenceList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.name.label', default: 'name')}"
                      value="${returnFromAbsenceList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.trackingInfo.dateCreatedUTC.label')}"
                      value="${returnFromAbsenceList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${returnFromAbsenceList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="sendDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'returnFromAbsenceList.transientData.sendDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="manualOutgoingNo"
                      size="8"
                      class="isRequired"
                      label="${message(code: 'returnFromAbsenceList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"
                      value=""/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>


<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['returnFromAbsenceRequestTableInReturnFromAbsenceList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>