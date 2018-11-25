<el:validatableModalForm title="${message(code: 'list.closeList.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="promotionList"
                         hideCancel="true"
                         action="closeList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${promotionList?.encodedId}" />

    <msg:modal/>

    <msg:warning isModal="true" label="${message(code: 'list.close.warning.message', default: 'Be aware that when you close the list, changes will be reflected on employee profile')}" />

    <g:if test="${promotionList?.promotionListEmployee?.find {
        it.recordStatus == ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.NEW
    }}">
        <msg:warning isModal="true" label="${g.message(code: 'list.warning.record.not.handled.message', default: 'some records still not handled.')}"/>
    </g:if>


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
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'promotionList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${promotionList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="closeDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'list.fromDate.close.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>