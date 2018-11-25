<el:validatableModalForm title="${message(code: 'list.receiveList.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="loanNoticeReplayList"
                         hideCancel="true"
                         action="receiveList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${loanNoticeReplayList?.encodedId}"/>

    <msg:modal/>

    <el:formGroup>
    <el:textField name="code"
                  size="8"
                  class=" "
                  label="${message(code: 'loanNoticeReplayList.code.label', default: 'code')}"
                  value="${loanNoticeReplayList?.code}"
                  isReadOnly="true"/>
</el:formGroup>


<el:formGroup>
    <el:textField name="name"
                  size="8"
                  class=" "
                  label="${message(code: 'loanNoticeReplayList.name.label', default: 'name')}"
                  value="${loanNoticeReplayList?.name}"
                  isReadOnly="true"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="trackingInfo.dateCreatedUTC"
                  size="8"
                  class=" "
                  label="${message(code: 'loanNoticeReplayList.trackingInfo.dateCreatedUTC.label')}"
                  value="${loanNoticeReplayList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                  isReadOnly="true"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate"
                  size="8"
                  class=" isRequired"
                  label="${message(code: 'loanNoticeReplayList.toDate.label')}"
                  value="${java.time.ZonedDateTime.now()}"/>
</el:formGroup>


<el:formGroup>
    <el:textField name="manualIncomeNo"
                  size="8"
                  class="isRequired"
                  label="${message(code: 'loanNoticeReplayList.manualIncomeNo.label', default: 'manualIncomeNo')}"
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