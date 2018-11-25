<el:validatableModalForm title="${message(code: 'list.sendList.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="maritalStatusList"
                         hideCancel="true"
                         action="sendList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${maritalStatusList?.encodedId}"></el:hiddenField>
    <msg:modal/>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'maritalStatusList.code.label', default: 'code')}"
                      value="${maritalStatusList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'maritalStatusList.name.label', default: 'name')}"
                      value="${maritalStatusList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'maritalStatusList.trackingInfo.dateCreatedUTC.label')}"
                      value="${maritalStatusList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'maritalStatusList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${maritalStatusList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'maritalStatusList.fromDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="manualOutgoingNo"
                      size="8"
                      class="isRequired"
                      label="${message(code: 'maritalStatusList.manualOutgoingNo.label', default: 'manualOutgoingNo')}"
                      value=""/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>


<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['maritalStatusRequestTableInMaritalStatusList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>