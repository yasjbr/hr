<el:validatableModalForm title="${message(code: 'list.closeList.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="dispatchList"
                         hideCancel="true"
                         action="closeList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${dispatchList?.encodedId}"/>

    <msg:modal/>



    <msg:warning isModal="true"
                 label="${message(code: 'list.close.warning.message', default: 'Be aware when you close the list the dispatch status will be reflected on employee profile')}"/>

    <g:if test="${dispatchList?.dispatchListEmployees?.find {
        it.recordStatus == ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.NEW
    }}">
        <msg:warning isModal="true"
                     label="${g.message(code: 'list.warning.record.not.handled.message', default: 'some records still not handled.')}"/>
    </g:if>


    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'dispatchList.code.label', default: 'code')}"
                      value="${dispatchList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'dispatchList.name.label', default: 'name')}"
                      value="${dispatchList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'dispatchList.trackingInfo.dateCreatedUTC.label')}"
                      value="${dispatchList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'dispatchList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${dispatchList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
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
    function callBackFunction(json) {
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>