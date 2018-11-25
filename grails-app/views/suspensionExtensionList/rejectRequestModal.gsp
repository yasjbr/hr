<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="suspensionExtensionList"
                         hideCancel="true"
                         hideClose="true"
                         action="rejectRequest" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${suspensionExtensionList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'suspensionExtensionList.code.label', default: 'code')}"
                      value="${suspensionExtensionList?.code}"
                      isReadOnly="true"/>
        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'suspensionExtensionList.name.label', default: 'name')}"
                      value="${suspensionExtensionList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'suspensionExtensionList.trackingInfo.dateCreatedUTC.label')}"
                      value="${suspensionExtensionList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'suspensionExtensionList.transientData.receiveDate.label')}"
                      value="${suspensionExtensionList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'suspensionExtensionListEmployeeNote.label')}</h4> <hr/></div>
    <g:render template="noteForm"/>

    <el:hiddenField name="check_suspensionExtensionRequestTableInSuspensionExtensionList" value=""/>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="close" onClick="cancelFunction()"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#check_suspensionExtensionRequestTableInSuspensionExtensionList").val(_dataTablesCheckBoxValues['suspensionExtensionRequestTableInSuspensionExtensionList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['suspensionExtensionRequestTableInSuspensionExtensionList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }

    function cancelFunction() {
        $('#application-modal-main-content').modal("hide");
    }
</script>