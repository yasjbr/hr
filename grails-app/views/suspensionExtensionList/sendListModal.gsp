<el:validatableModalForm title="${message(code: 'list.sendList.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="suspensionExtensionList"
                         hideCancel="true"
                         action="sendList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${suspensionExtensionList?.encodedId}"></el:hiddenField>
    <msg:modal/>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionExtensionList.code.label', default: 'code')}"
                      value="${suspensionExtensionList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionExtensionList.name.label', default: 'name')}"
                      value="${suspensionExtensionList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionExtensionList.trackingInfo.dateCreatedUTC.label')}"
                      value="${suspensionExtensionList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionExtensionList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${suspensionExtensionList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'suspensionExtensionList.fromDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="manualOutgoingNo"
                      size="8"
                      class="isRequired"
                      label="${message(code: 'suspensionExtensionList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"
                      value=""/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>


<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['suspensionExtensionRequestTableInSuspensionExtensionList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>