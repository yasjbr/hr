<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="generalList"
                         hideCancel="true"
                         hideClose="true"
                         action="rejectRequest" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${generalList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'generalList.code.label', default: 'code')}"
                      value="${generalList?.code}"
                      isReadOnly="true"/>
        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'generalList.name.label', default: 'name')}"
                      value="${generalList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'generalList.trackingInfo.dateCreatedUTC.label')}"
                      value="${generalList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'generalList.transientData.receiveDate.label')}"
                      value="${generalList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'generalListEmployeeNote.label')}</h4> <hr/></div>
    <g:render template="noteForm"/>

    <el:hiddenField name="check_employeeTableInGeneralList" value=""/>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="close" onClick="cancelFunction()"/>

</el:validatableModalForm>
<script type="text/javascript">


    $("#check_employeeTableInGeneralList").val(_dataTablesCheckBoxValues['employeeTableInGeneralList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['employeeTableInGeneralList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }

    function cancelFunction() {
        $('#application-modal-main-content').modal("hide");
    }
</script>