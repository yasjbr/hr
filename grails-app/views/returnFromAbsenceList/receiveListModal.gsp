<el:validatableModalForm title="${message(code: 'list.receiveList.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="returnFromAbsenceList"
                         hideCancel="true"
                         action="receiveList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${returnFromAbsenceList?.encodedId}"/>

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
    <el:dateField name="receiveDate"
                  size="8"
                  class=" isRequired"
                  label="${message(code: 'returnFromAbsenceList.transientData.receiveDate.label')}"
                  value="${java.time.ZonedDateTime.now()}"/>
</el:formGroup>


<el:formGroup>
    <el:textField name="manualIncomeNo"
                  size="8"
                  class="isRequired"
                  label="${message(code: 'returnFromAbsenceList.manualIncomeNo.label', default: 'manualIncomeNo')}"
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