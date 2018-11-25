<el:validatableModalForm title="${message(code: 'list.sendList.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="applicantInspectionResultList"
                         hideCancel="true"
                         action="sendList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${applicantInspectionResultList?.encodedId}"></el:hiddenField>
    <msg:modal/>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'applicantInspectionResultList.code.label', default: 'code')}"
                      value="${applicantInspectionResultList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'applicantInspectionResultList.name.label', default: 'name')}"
                      value="${applicantInspectionResultList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'applicantInspectionResultList.trackingInfo.dateCreatedUTC.label')}"
                      value="${applicantInspectionResultList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'applicantInspectionResultList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${applicantInspectionResultList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'applicantInspectionResultList.fromDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="manualOutgoingNo"
                      size="8"
                      class="isRequired"
                      label="${message(code: 'applicantInspectionResultList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"
                      value=""/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>


<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['applicantTableInApplicantInspectionResultList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>