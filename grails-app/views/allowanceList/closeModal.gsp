<el:validatableModalForm title="${message(code: 'allowanceList.closeAllowanceList.label')}"
                         width="60%"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="allowanceList" action="closeList">
    <msg:modal/>

    <el:hiddenField name="encodedId" value="${allowanceList?.encodedId}"/>

    <g:if test="${allowanceList?.allowanceListEmployee?.find {
        it.recordStatus == ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.NEW
    }}">
        <msg:warning isModal="true" label="${g.message(code: 'list.warning.record.not.handled.message', default: 'some records still not handled.')}"/>
    </g:if>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'allowanceList.code.label', default: 'code')}"
                      value="${allowanceList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>



    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'allowanceList.name.label', default: 'name')}"
                      value="${allowanceList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>



    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'allowanceList.trackingInfo.dateCreatedUTC.label')}"
                      value="${allowanceList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'allowanceList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${allowanceList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" isRequired"
                      label="${message(code: 'allowanceList.currentStatus.closeDate.label')}"
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