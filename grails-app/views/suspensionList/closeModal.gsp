<el:validatableModalForm title="${message(code: 'suspensionList.closeSuspensionList.label')}"
                         width="60%"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="suspensionList" action="closeList">
    <msg:modal/>

    <el:hiddenField name="encodedId" value="${suspensionList?.encodedId}"/>

    <g:if test="${suspensionList?.suspensionListEmployees?.find {
        it.recordStatus == ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.NEW
    }}">
        <msg:warning isModal="true" label="${g.message(code: 'list.warning.record.not.handled.message', default: 'some records still not handled.')}"/>
    </g:if>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.code.label', default: 'code')}"
                      value="${suspensionList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>



    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.name.label', default: 'name')}"
                      value="${suspensionList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>



    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.trackingInfo.dateCreatedUTC.label')}"
                      value="${suspensionList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${suspensionList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'suspensionList.currentStatus.closeDate.label')}"
                      value="${java.time.ZonedDateTime.now()}"/>
    </el:formGroup>
    <el:row/>
    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            window.location.reload();
        }
    }
</script>