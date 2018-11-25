<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="70%"
                         name="approveRequestForm"
                         controller="returnFromAbsenceList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToApproved" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${returnFromAbsenceList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <msg:warning label="${message(code: 'dispatchList.approve.warning.message', default: 'Warning')}"/>
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.code.label', default: 'code')}"
                      value="${returnFromAbsenceList?.code}"
                      isReadOnly="true"/>
        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.name.label', default: 'name')}"
                      value="${returnFromAbsenceList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.trackingInfo.dateCreatedUTC.label')}"
                      value="${returnFromAbsenceList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.transientData.receiveDate.label')}"
                      value="${returnFromAbsenceList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'returnFromAbsenceListEmployeeNote.label')}</h4> <hr/></div>

    <el:formGroup>
        <el:hiddenField name="save_returnFromAbsenceListEmployeeId"/>
        <el:textField name="orderNo" size="6" class=""
                      label="${message(code: 'returnFromAbsenceListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
        <el:dateField name="noteDate" size="6" class=""
                      label="${message(code: 'returnFromAbsenceListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'returnFromAbsenceListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:hiddenField name="check_RequestIdList" value=""/>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#check_RequestIdList").val(_dataTablesCheckBoxValues['returnFromAbsenceRequestTableInReturnFromAbsenceList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['returnFromAbsenceRequestTableInReturnFromAbsenceList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>