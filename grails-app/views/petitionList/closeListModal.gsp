<el:validatableModalForm title="${message(code: 'list.closeList.label')}"
                         width="70%"
                         name="sendDataForm"
                         controller="petitionList"
                         hideCancel="true"
                         action="closeList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${petitionList?.encodedId}"/>

    <msg:modal/>


    <g:if test="${petitionList?.petitionListEmployees?.find {
        it.recordStatus == ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.NEW
    }}">
        <msg:warning isModal="true"
                     label="${g.message(code: 'list.warning.record.not.handled.message', default: 'some records still not handled.')}"/>
    </g:if>



    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'petitionList.code.label', default: 'code')}"
                      value="${petitionList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'petitionList.name.label', default: 'name')}"
                      value="${petitionList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'petitionList.trackingInfo.dateCreatedUTC.label')}"
                      value="${petitionList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'petitionList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${petitionList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
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
            _dataTables['petitionRequestTableInPetitionList'].draw();
            $('#application-modal-main-content').modal("hide");
            window.location.reload();
        }
    }
</script>