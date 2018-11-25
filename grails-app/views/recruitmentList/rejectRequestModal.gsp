<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="60%"
                         name="rejectApplicantForm"
                         controller="recruitmentList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeApplicantToRejected" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${recruitmentList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'recruitmentList.code.label', default: 'code')}"
                      value="${recruitmentList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'recruitmentList.name.label', default: 'name')}"
                      value="${recruitmentList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'recruitmentList.trackingInfo.dateCreatedUTC.label')}"
                      value="${recruitmentList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="toDate"
                      size="8"
                      class=" "
                      label="${message(code: 'recruitmentList.transientData.receiveDate.label')}"
                      value="${recruitmentList.transientData.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'recruitmentListEmployeeNote.label')}</h4> <hr/></div>
    <el:formGroup>
        <el:hiddenField name="save_recruitmentListEmployeeId"/>

        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'recruitmentListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'recruitmentListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>

    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'recruitmentListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:row/>

    <el:hiddenField name="check_applicantTableInRecruitmentList" value=""/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#check_applicantTableInRecruitmentList").val(_dataTablesCheckBoxValues['applicantTableInRecruitmentList']);
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>