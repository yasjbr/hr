<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<el:validatableModalForm title="${message(code: 'list.closeList.label')}"
                         width="60%"
                         name="sendDataForm"
                         controller="loanList"
                         hideCancel="true"
                         action="closeList" callBackFunction="callBackFunction">

    <el:hiddenField name="encodedId" id="encodedId" value="${loanList?.encodedId}"/>

    <msg:modal/>
    <g:if test="${loanList?.loanListPerson?.loanRequest?.find {
        it.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST
    }}">
        <msg:warning isModal="true"
                     label="${g.message(code: 'list.warning.record.not.handled.message', default: 'some records still not handled.')}"/>
    </g:if>

    <msg:warning isModal="true"
                 label="${message(code: 'list.close.warning.message', default: 'Be aware that when you close the list, changes will be reflected on employee profile')}"/>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'loanList.code.label', default: 'code')}"
                      value="${loanList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'loanList.name.label', default: 'name')}"
                      value="${loanList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'loanList.trackingInfo.dateCreatedUTC.label')}"
                      value="${loanList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="trackingInfo.lastUpdatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'loanList.trackingInfo.lastUpdatedUTC.label')}"
                      value="${loanList?.trackingInfo?.lastUpdatedUTC?.dateTime?.date}"
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
            window.location.href = "${createLink(controller: 'loanList', action: 'list')}";
        }
    }
</script>