<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="70%"
                         name="approveRequestForm"
                         controller="petitionList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToApproved" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${petitionList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <msg:warning label="${message(code: 'dispatchList.approve.warning.message', default: 'Warning')}"/>
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'petitionList.code.label', default: 'code')}"
                      value="${petitionList?.code}"
                      isReadOnly="true"/>
        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'petitionList.name.label', default: 'name')}"
                      value="${petitionList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'petitionList.trackingInfo.dateCreatedUTC.label')}"
                      value="${petitionList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'petitionList.transientData.receiveDate.label')}"
                      value="${petitionList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'petitionListEmployeeNote.label')}</h4> <hr/></div>

    <el:formGroup>
        <el:hiddenField name="save_petitionListEmployeeId"/>
        <el:textField name="orderNo" size="6" class=""
                      label="${message(code: 'petitionListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
        <el:dateField name="noteDate" size="6" class=""
                      label="${message(code: 'petitionListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'petitionListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:hiddenField name="check_RequestIdList" value=""/>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#check_RequestIdList").val(_dataTablesCheckBoxValues['petitionRequestTableInPetitionList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['petitionRequestTableInPetitionList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>