<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="serviceList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToApproved" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${serviceList?.encodedId}"></el:hiddenField>

    <msg:modal/>

    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'serviceList.code.label', default: 'code')}"
                      value="${serviceList?.code}"
                      isReadOnly="true"/>
        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'serviceList.name.label', default: 'name')}"
                      value="${serviceList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'serviceList.trackingInfo.dateCreatedUTC.label')}"
                      value="${serviceList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="receiveDate"
                      size="6"
                      class=" "
                      label="${message(code: 'serviceList.transientData.receiveDate.label')}"
                      value="${serviceList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="orderNo" size="6" class=" isRequired"
                      label="${message(code: 'serviceListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
        <el:dateField name="noteDate" size="6" class=" isRequired"
                      label="${message(code: 'serviceListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>

        <g:render template="recordAcceptForm" />

        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'serviceListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:row/>

    <el:hiddenField name="checked_serviceListEmployeeIdsList" value="" />

    <el:formButton isSubmit="true" functionName="save"/>

    <el:row/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#checked_serviceListEmployeeIdsList").val(_dataTablesCheckBoxValues['employmentServiceTableInServiceList']);
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>