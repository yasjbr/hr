<el:validatableModalForm title="${message(code: 'list.closeList.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="violationList"
                         hideCancel="true"
                         action="closeList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${violationList?.encodedId}" />

    <msg:modal/>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'violationList.code.label', default: 'code')}"
                      value="${violationList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'violationList.name.label', default: 'name')}"
                      value="${violationList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'violationList.trackingInfo.dateCreatedUTC.label')}"
                      value="${violationList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'violationList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${violationList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'list.fromDate.close.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['employeeViolationTableInViolationList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>