<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="loanNoticeReplayList"
                         hideCancel="true"
                         hideClose="true"
                         action="approveRequest" callBackFunction="callBackFunction">
    <el:hiddenField name="encodedId" id="encodedId" value="${loanNoticeReplayList?.encodedId}" />
    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'loanNoticeReplayList.code.label', default: 'code')}"
                      value="${loanNoticeReplayList?.code}"
                      isReadOnly="true"/>

        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'loanNoticeReplayList.name.label', default: 'name')}"
                      value="${loanNoticeReplayList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'loanNoticeReplayList.trackingInfo.dateCreatedUTC.label')}"
                      value="${loanNoticeReplayList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>

        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'loanNoticeReplayList.transientData.receiveDate.label')}"
                      value="${loanNoticeReplayList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <g:render template="recordAcceptForm" />

    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'loanNominatedEmployeeNote.label')}
        </h4>
        <hr/>
    </div>

    <el:formGroup>
        <el:textField name="orderNo" size="6" class=" isRequired"
                      label="${message(code: 'loanNominatedEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>

        <el:dateField name="noteDate" size="6" class=" isRequired"
                      label="${message(code: 'loanNominatedEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'loanNominatedEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>


    <el:row/>

    <el:hiddenField name="checked_loanNominatedEmployeeIdsList" value="" />

    <el:formButton isSubmit="true" functionName="save"/>

    <el:row/>

</el:validatableModalForm>

<script type="text/javascript">

    $("#checked_loanNominatedEmployeeIdsList").val(_dataTablesCheckBoxValues['loanNominatedEmployeeTable']);
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }

</script>
