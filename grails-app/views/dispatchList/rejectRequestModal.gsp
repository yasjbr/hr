<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="dispatchList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToRejected" callBackFunction="callBackFunction">

    <el:hiddenField name="dispatchList.id" value="${dispatchList?.id}"></el:hiddenField>

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
                      class=" "
                      label="${message(code: 'dispatchList.transientData.receiveDate.label')}"
                      value="${dispatchList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'dispatchListEmployeeNote.label')}</h4> <hr/></div>
    <el:formGroup>
        <el:hiddenField name="save_dispatchListEmployeeId"/>

        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'dispatchListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'dispatchListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'dispatchListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:row/>

    <el:hiddenField name="check_dispatchRequestTableInDispatchList" value="" />

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#check_dispatchRequestTableInDispatchList").val(_dataTablesCheckBoxValues['dispatchRequestTableInDispatchList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['dispatchRequestTableInDispatchList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>