<el:validatableModalForm title="${message(code: 'allowanceList.receiveAllowanceList.label')}"
                         width="60%"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="allowanceList" action="receiveData">
    <msg:modal/>

    <el:hiddenField name="encodedId" value="${allowanceList?.encodedId}"/>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'allowanceList.code.label', default: 'code')}"
                      value="${allowanceList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'allowanceList.name.label', default: 'name')}"
                      value="${allowanceList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'allowanceList.trackingInfo.dateCreatedUTC.label')}"
                      value="${allowanceList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'allowanceList.currentStatus.toDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="manualIncomeNo"
                      size="8"
                      class="isRequired"
                      label="${message(code: 'allowanceList.manualIncomeNo.label', default: 'manualIncomeNo')}"
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