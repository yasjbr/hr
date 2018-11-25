<el:validatableModalForm title="${message(code: 'list.addExceptional.label')}"
                         name="receiveListForm" callBackFunction="callBackFunction"
                         controller="externalTransferListEmployee" action="save">

    <msg:modal/>
    <el:hiddenField name="externalTransferList.id" value="${id}"/>

    <g:render template="/externalTransferListEmployee/form"/>


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