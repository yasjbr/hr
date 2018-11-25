<el:validatableModalForm title="${message(code: 'vacationList.rejectVacationList.label')}"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="vacationList" action="rejectRequest">
    <msg:modal/>

    <el:hiddenField name="encodedId" value="${vacationList?.encodedId}"/>




    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'vacationList.code.label', default: 'code')}"
                      value="${vacationList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'vacationList.name.label', default: 'name')}"
                      value="${vacationList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>



    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'vacationList.trackingInfo.dateCreatedUTC.label')}"
                      value="${vacationList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" "
                      label="${message(code: 'vacationList.currentStatus.toDate.label')}"
                      value="${vacationList?.currentStatus?.fromDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'vacationListEmployeeNote.label')}</h4> <hr/></div>
    <g:render template="noteForm" model="[isRequired: 'isRequired']"/>

    <el:row/>

    <el:formButton isSubmit="true" functionName="save"/>

</el:validatableModalForm>

<script type="text/javascript">
    function callBackFunction(json) {
        if (json.success) {
            window.location.reload();
        }
    }

    $(document).ready(function () {
        var theForm = document.forms['receiveListForm'];
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = "check_vacationRequestTableInVacationList";
        input.value = _dataTablesCheckBoxValues['vacationRequestTableInVacationList']
        theForm.appendChild(input);
    });
</script>