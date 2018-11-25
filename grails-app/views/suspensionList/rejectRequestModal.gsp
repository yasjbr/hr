<el:validatableModalForm title="${message(code: 'suspensionList.rejectSuspensionList.label')}"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="suspensionList" action="rejectRequest">
    <msg:modal/>

    <el:hiddenField name="encodedId" value="${suspensionList?.encodedId}"/>




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
        <el:dateField name="fromDate"
                      size="8"
                      class=" "
                      label="${message(code: 'suspensionList.currentStatus.toDate.label')}"
                      value="${suspensionList?.currentStatus?.fromDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'suspensionListEmployeeNote.label')}</h4> <hr/></div>
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
        input.name = "check_suspensionRequestTableInSuspensionList";
        input.value = _dataTablesCheckBoxValues['suspensionRequestTableInSuspensionList']
        theForm.appendChild(input);
    });
</script>