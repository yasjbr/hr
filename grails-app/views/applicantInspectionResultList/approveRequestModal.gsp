<el:validatableModalForm title="${message(code: 'list.approveRequest.label')}"
                         width="70%"
                         name="approveRequestForm"
                         controller="applicantInspectionResultList"
                         hideCancel="true"
                         hideClose="true"
                         action="approveRequest" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${applicantInspectionResultList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="6"
                      class=" "
                      label="${message(code: 'applicantInspectionResultList.code.label', default: 'code')}"
                      value="${applicantInspectionResultList?.code}"
                      isReadOnly="true"/>
        <el:textField name="name"
                      size="6"
                      class=" "
                      label="${message(code: 'applicantInspectionResultList.name.label', default: 'name')}"
                      value="${applicantInspectionResultList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="6"
                      class=" "
                      label="${message(code: 'applicantInspectionResultList.trackingInfo.dateCreatedUTC.label')}"
                      value="${applicantInspectionResultList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
        <el:dateField name="toDate"
                      size="6"
                      class=" "
                      label="${message(code: 'applicantInspectionResultList.transientData.receiveDate.label')}"
                      value="${applicantInspectionResultList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'applicantInspectionResultListEmployeeNote.label')}</h4> <hr/></div>

    <el:formGroup>
        <el:hiddenField name="save_applicantInspectionResultListEmployeeId"/>
        <el:textField name="orderNo" size="6" class=""
                      label="${message(code: 'applicantInspectionResultListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
        <el:dateField name="noteDate" size="6" class=""
                      label="${message(code: 'applicantInspectionResultListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="6" class=""
                     label="${message(code: 'applicantInspectionResultListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:hiddenField name="check_applicantTableInApplicantInspectionResultList" value=""/>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

    <el:formButton functionName="close" onClick="cancelFunction()"/>

</el:validatableModalForm>

<script type="text/javascript">


    $("#check_applicantTableInApplicantInspectionResultList").val(_dataTablesCheckBoxValues['applicantTableInApplicantInspectionResultList']);
    function callBackFunction(json) {
        if (json.success) {
            _dataTables['applicantTableInApplicantInspectionResultList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }

    function cancelFunction() {
        $('#application-modal-main-content').modal("hide");
    }
</script>