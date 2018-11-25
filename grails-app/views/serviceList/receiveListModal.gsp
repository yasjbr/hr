<el:validatableModalForm title="${message(code: 'list.receiveList.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="serviceList"
                         hideCancel="true"
                         action="receiveList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${serviceList?.encodedId}"/>

    <msg:modal/>

    <el:formGroup>
    <el:textField name="code"
                  size="8"
                  class=" "
                  label="${message(code: 'serviceList.code.label', default: 'code')}"
                  value="${serviceList?.code}"
                  isReadOnly="true"/>
</el:formGroup>


<el:formGroup>
    <el:textField name="name"
                  size="8"
                  class=" "
                  label="${message(code: 'serviceList.name.label', default: 'name')}"
                  value="${serviceList?.name}"
                  isReadOnly="true"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="trackingInfo.dateCreatedUTC"
                  size="8"
                  class=" "
                  label="${message(code: 'serviceList.trackingInfo.dateCreatedUTC.label')}"
                  value="${serviceList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                  isReadOnly="true"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate"
                  size="8"
                  class=" isRequired"
                  label="${message(code: 'serviceList.toDate.label')}"
                  value="${java.time.ZonedDateTime.now()}"/>
</el:formGroup>


<el:formGroup>
    <el:textField name="manualIncomeNo"
                  size="8"
                  class="isRequired"
                  label="${message(code: 'serviceList.manualIncomeNo.label', default: 'manualIncomeNo')}"
                  value=""/>
</el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>


<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>