<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="returnFromAbsenceList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToRejected" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${returnFromAbsenceList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.code.label', default: 'code')}"
                      value="${returnFromAbsenceList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.name.label', default: 'name')}"
                      value="${returnFromAbsenceList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'returnFromAbsenceList.trackingInfo.dateCreatedUTC.label')}"
                      value="${returnFromAbsenceList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="toDate"
                      size="8"
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
    <g:render template="noteForm"/>

    <el:hiddenField name="check_RequestIdList" value="" />

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