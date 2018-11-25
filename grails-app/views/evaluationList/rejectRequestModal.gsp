<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="evaluationList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToRejected" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${evaluationList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'evaluationList.code.label', default: 'code')}"
                      value="${evaluationList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'evaluationList.name.label', default: 'name')}"
                      value="${evaluationList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'evaluationList.trackingInfo.dateCreatedUTC.label')}"
                      value="${evaluationList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="toDate"
                      size="8"
                      class=" "
                      label="${message(code: 'evaluationList.transientData.receiveDate.label')}"
                      value="${evaluationList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'evaluationListEmployeeNote.label')}</h4> <hr/></div>
    <g:render template="noteForm"/>

    <el:hiddenField name="check_employeeEvaluationTableInEvaluationList" value="" />

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#check_employeeEvaluationTableInEvaluationList").val(_dataTablesCheckBoxValues['employeeEvaluationTableInEvaluationList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['employeeEvaluationTableInEvaluationList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>