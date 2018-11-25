<el:validatableModalForm title="${message(code: 'externalTransferList.rejectExternalTransferList.label')}"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="externalTransferList" action="rejectRequest">

    <el:hiddenField name="id" value="${externalTransferList?.id}"/>

    <msg:modal/>

    <el:formGroup>
        <el:textField name="code"
                      size="8"
                      class=" "
                      label="${message(code: 'externalTransferList.code.label', default: 'code')}"
                      value="${externalTransferList?.code}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:textField name="name"
                      size="8"
                      class=" "
                      label="${message(code: 'externalTransferList.name.label', default: 'name')}"
                      value="${externalTransferList?.name}"
                      isReadOnly="true"/>
    </el:formGroup>




    <el:formGroup>
        <el:textField name="trackingInfo.dateCreatedUTC"
                      size="8"
                      class=" "
                      label="${message(code: 'externalTransferList.trackingInfo.dateCreatedUTC.label')}"
                      value="${externalTransferList?.trackingInfo?.dateCreatedUTC?.dateTime?.date}"
                      isReadOnly="true"/>
    </el:formGroup>

    <el:formGroup>
        <el:dateField name="fromDate"
                      size="8"
                      class=" "
                      label="${message(code: 'externalTransferList.currentStatus.toDate.label')}"
                      value="${externalTransferList?.currentStatus?.fromDate}"
                      isReadOnly="true"/>
    </el:formGroup>
    <el:row/>
    <el:row/>
    <div style="padding-right: 40px;,padding-bottom: 15px;">
        <h4 class=" smaller lighter blue">
            ${message(code: 'externalTransferListEmployeeNote.label')}</h4> <hr/></div>
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
        input.name = "check_externalTransferRequestTableInExternalTransferList";
        input.value = _dataTablesCheckBoxValues['externalTransferRequestTableInExternalTransferList']
        theForm.appendChild(input);
    });
</script>