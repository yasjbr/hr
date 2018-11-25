<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="childList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToRejected" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${childList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'childList.code.label', default: 'code')}"
                      value="${childList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'childList.name.label', default: 'name')}"
                      value="${childList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'childList.trackingInfo.dateCreatedUTC.label')}"
                      value="${childList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="toDate"
                      size="8"
                      class=" "
                      label="${message(code: 'childList.transientData.receiveDate.label')}"
                      value="${childList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'childListEmployeeNote.label')}</h4> <hr/></div>
    <g:render template="noteForm"/>

    <el:hiddenField name="check_childRequestTableInChildList" value="" />

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#check_childRequestTableInChildList").val(_dataTablesCheckBoxValues['childRequestTableInChildList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['childRequestTableInChildList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>