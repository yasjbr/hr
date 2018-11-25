<el:validatableModalForm title="${message(code: 'suspensionList.sendSuspensionList.label')}"
                         width="60%"
                         name="sendListForm" callBackFunction="callBackFunction"
                         controller="suspensionList" action="sendData">
    <msg:modal/>
    <el:hiddenField name="encodedId" value="${suspensionList?.encodedId}"/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.code.label', default: 'code')}"
                      value="${suspensionList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.name.label', default: 'name')}"
                      value="${suspensionList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>




    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.trackingInfo.dateCreatedUTC.label')}"
                      value="${suspensionList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${suspensionList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'suspensionList.currentStatus.fromDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="manualOutgoingNo"
                      size="8"
                      class="isRequired"
                      label="${message(code: 'suspensionList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"
                      value=""/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>
</el:validatableModalForm>

<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            window.location.reload();
        }
    }
</script>