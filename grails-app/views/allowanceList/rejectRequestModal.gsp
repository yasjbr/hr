<el:validatableModalForm title="${message(code: 'allowanceList.rejectAllowanceList.label')}"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="allowanceList" action="rejectRequest">

    <el:hiddenField name="encodedId" value="${allowanceList?.encodedId}"/>

    <msg:modal/>


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
        <el:dateField name="fromDate"
                      size="8"
                      class=" "
                      label="${message(code: 'allowanceList.currentStatus.toDate.label')}"
                      value="${allowanceList?.currentStatus?.fromDate}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'allowanceListEmployeeNote.label')}</h4> <hr/></div>
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
        input.name = "check_allowanceRequestTableInAllowanceList";
        input.value = _dataTablesCheckBoxValues['allowanceRequestTableInAllowanceList']
        theForm.appendChild(input);
    });
</script>