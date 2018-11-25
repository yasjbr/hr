<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="60%"
                         name="rejectRequestForm"
                         controller="serviceList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToRejected" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${serviceList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'serviceList.code.label', default: 'code')}"
                      value="${serviceList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'serviceList.name.label', default: 'name')}"
                      value="${serviceList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'serviceList.trackingInfo.dateCreatedUTC.label')}"
                      value="${serviceList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="toDate"
                      size="8"
                      class=" "
                      label="${message(code: 'serviceList.transientData.receiveDate.label')}"
                      value="${serviceList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'serviceListEmployeeNote.label')}</h4> <hr/></div>
    <el:formGroup>
        <el:hiddenField name="save_serviceListEmployeeId"/>

        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'serviceListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'serviceListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>

    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'serviceListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:row/>

    <el:hiddenField name="checked_serviceListEmployeeIdsList" value=""/>

    <el:formButton isSubmit="true" functionName="save"/>

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