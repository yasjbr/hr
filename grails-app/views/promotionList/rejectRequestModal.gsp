<el:validatableModalForm title="${message(code: 'list.rejectRequest.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="promotionList"
                         hideCancel="true"
                         hideClose="true"
                         action="changeRequestToRejected" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${promotionList?.encodedId}"></el:hiddenField>

    <msg:modal/>
    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'promotionList.code.label', default: 'code')}"
                      value="${promotionList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>


    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'promotionList.name.label', default: 'name')}"
                      value="${promotionList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'promotionList.trackingInfo.dateCreatedUTC.label')}"
                      value="${promotionList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="toDate"
                      size="8"
                      class=" "
                      label="${message(code: 'promotionList.transientData.receiveDate.label')}"
                      value="${promotionList?.transientData?.receiveDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'promotionListEmployeeNote.label')}</h4> <hr/></div>
    <el:formGroup>
        <el:hiddenField name="save_promotionListEmployeeId"/>

        <el:textField name="orderNo" size="8" class=""
                      label="${message(code: 'promotionListEmployeeNote.orderNo.label', default: 'orderNo')}"
                      value=""/>
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'promotionListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class=""
                     label="${message(code: 'promotionListEmployeeNote.note.label', default: 'note')}"
                     value=""/>
    </el:formGroup>

    <el:row/>

    <el:hiddenField name="checked_promotionEmployeeIdsList" value="" />

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    $("#checked_promotionEmployeeIdsList").val(_dataTablesCheckBoxValues['promotionListEmployeeTableInPromotionList']);
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>