<el:validatableModalForm title="${message(code: 'list.closeList.label')}"
                         width="60%"
                         name="closeListForm"
                         controller="recruitmentList"
                         hideCancel="true"
                         action="closeList" callBackFunction="callBackOnSuccess">

    <el:hiddenField name="encodedId" id="encodedId" value="${recruitmentList?.encodedId}" />

    <msg:modal/>

    <g:if test="${recruitmentList?.recruitmentListEmployees?.find {
        it.recordStatus == ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.NEW
    }}">
        <msg:warning isModal="true" label="${g.message(code: 'list.warning.record.not.handled.message', default: 'some records still not handled.')}"/>
    </g:if>


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
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'recruitmentList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${recruitmentList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'list.fromDate.close.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>


<script type="text/javascript">
    function callBackOnSuccess(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.href="${createLink(controller: 'recruitmentList', action: 'list')}";
        }
    }
</script>
