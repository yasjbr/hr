<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="loanList"
                         hideCancel="true"
                         hideClose="true"
                         action="rejectRequest" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${loanList?.encodedId}" />

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'loanList.code.label', default: 'code')}"
                      value="${loanList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'loanList.name.label', default: 'name')}"
                      value="${loanList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'loanList.trackingInfo.dateCreatedUTC.label')}"
                      value="${loanList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="toDate"
                      size="8"
                      class=" "
                      label="${message(code: 'loanList.toDate.label')}"
                      value="${loanList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'loanListPersonNote.label')}</h4> <hr/></div>
    <el:formGroup>
        <el:hiddenField name="save_loanListPersonId"/>

        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'loanListPersonNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'loanListPersonNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'loanListPersonNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:row/>

    <el:hiddenField name="checked_loanPersonIdsList" value="" />

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#checked_loanPersonIdsList").val(_dataTablesCheckBoxValues['loanListPersonTable']);
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>