<el:validatableModalForm title="${message(code: 'list.receiveList.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="dispatchList"
                         hideCancel="true"
                         action="receiveList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${dispatchList?.encodedId}"/>

    <msg:modal/>

    <el:formGroup>
    <el:textField name="code"
                  size="8"
                  class=" "
                  label="${message(code: 'dispatchList.code.label', default: 'code')}"
                  value="${dispatchList?.code}"
                  isReadOnly="true"/>
</el:formGroup>


<el:formGroup>
    <el:textField name="name"
                  size="8"
                  class=" "
                  label="${message(code: 'dispatchList.name.label', default: 'name')}"
                  value="${dispatchList?.name}"
                  isReadOnly="true"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="trackingInfo.dateCreatedUTC"
                  size="8"
                  class=" "
                  label="${message(code: 'dispatchList.trackingInfo.dateCreatedUTC.label')}"
                  value="${dispatchList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                  isReadOnly="true"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate"
                  size="8"
                  class=" isRequired"
                  label="${message(code: 'dispatchList.toDate.label')}"
                  value="${java.time.ZonedDateTime.now()}"/>
</el:formGroup>


<el:formGroup>
    <el:textField name="manualIncomeNo"
                  size="8"
                  class="isRequired"
                  label="${message(code: 'dispatchList.manualIncomeNo.label', default: 'manualIncomeNo')}"
                  value=""/>
</el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>


<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['dispatchRequestTableInDispatchList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>