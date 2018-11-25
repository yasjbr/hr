<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="70%"
                         name="approveRequestForm"
                         controller="maritalStatusList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToApproved" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${maritalStatusList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <msg:warning label="${message(code: 'dispatchList.approve.warning.message', default: 'Warning')}"/>
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'maritalStatusList.code.label', default: 'code')}"
                      value="${maritalStatusList?.code}"
                      isReadOnly="true"/>
        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'maritalStatusList.name.label', default: 'name')}"
                      value="${maritalStatusList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'maritalStatusList.trackingInfo.dateCreatedUTC.label')}"
                      value="${maritalStatusList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'maritalStatusList.transientData.receiveDate.label')}"
                      value="${maritalStatusList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <g:render template="recordAcceptForm" />

     <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'maritalStatusEmployeeNote.label')}</h4> <hr/></div>

    <el:formGroup>
        <el:hiddenField name="save_maritalStatusListEmployeeId"/>
        <el:textField name="orderNo" size="6" class=""
                      label="${message(code: 'maritalStatusListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
        <el:dateField name="noteDate" size="6" class=""
                      label="${message(code: 'maritalStatusListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'maritalStatusListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:hiddenField name="check_requestTableInList" value=""/>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">

    $("#check_requestTableInList").val(_dataTablesCheckBoxValues['maritalStatusRequestTableInMaritalStatusList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['maritalStatusRequestTableInMaritalStatusList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>