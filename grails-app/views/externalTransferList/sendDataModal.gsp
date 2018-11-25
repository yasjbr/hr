<el:validatableModalForm title="${message(code: 'externalTransferList.sendExternalTransferList.label')}"
                         width="60%"
                         name="sendListForm" callBackFunction="callBackFunction"
                         controller="externalTransferList" action="sendData">
    <msg:modal/>
    <el:hiddenField name="encodedId" value="${externalTransferList?.encodedId}"/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'externalTransferList.code.label', default: 'code')}"
                      value="${externalTransferList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'externalTransferList.name.label', default: 'name')}"
                      value="${externalTransferList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>




    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'externalTransferList.trackingInfo.dateCreatedUTC.label')}"
                      value="${externalTransferList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'externalTransferList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${externalTransferList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'externalTransferList.currentStatus.fromDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="manualOutgoingNo"
                      size="8"
                      class="isRequired"
                      label="${message(code: 'externalTransferList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"
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