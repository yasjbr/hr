<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="70%"
                         name="approveRequestForm"
                         controller="evaluationList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToApproved" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${evaluationList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <msg:warning label="${message(code: 'dispatchList.approve.warning.message', default: 'Warning')}"/>
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'evaluationList.code.label', default: 'code')}"
                      value="${evaluationList?.code}"
                      isReadOnly="true"/>
        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'evaluationList.name.label', default: 'name')}"
                      value="${evaluationList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'evaluationList.trackingInfo.dateCreatedUTC.label')}"
                      value="${evaluationList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="toDate"
                      size="6"
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

    <el:formGroup>
        <el:hiddenField name="save_evaluationListEmployeeId"/>
        <el:textField name="orderNo" size="6" class=""
                      label="${message(code: 'evaluationListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
        <el:dateField name="noteDate" size="6" class=""
                      label="${message(code: 'evaluationListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'evaluationListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:hiddenField name="check_employeeEvaluationTableInEvaluationList" value=""/>

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