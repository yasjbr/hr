<script type="text/javascript">
    function callBackSuccess(json) {
        $('#suspensionRequestTable').find('a.green.modal-ajax_${suspensionExtensionRequest?.suspensionRequest?.id}').click();

    }
</script>
<g:set var="suspensionRequestFormEntity"
       value="${message(code: 'suspensionExtensionRequest.entity', default: 'suspensionExtensionRequest')}"/>
<g:set var="suspensionRequestFormTitle"
       value="${message(code: 'default.edit.label', args: [suspensionRequestFormEntity], default: 'suspensionExtensionRequest')}"/>

<el:validatableModalForm title="${suspensionRequestFormTitle}" callBackFunction="callBackSuccess"
                         width="70%"
                         name="suspensionRequestForm"
                         controller="suspensionExtensionRequest"
                         hideCancel="true"
                         hideClose="true"
                         action="update">
    <msg:modal/>
    <el:hiddenField name="id" value="${suspensionExtensionRequest?.id}"/>

    <g:render template="/suspensionExtensionRequest/form"/>


    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton onClick="closeForm()" functionName="cancel"/>

</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#suspensionRequestForm').length;
        if (isCreate > 0) {
            $('#suspensionRequestTable').find('a.green.modal-ajax_${suspensionExtensionRequest?.suspensionRequest?.id}').click();
        }
    });

    function closeForm() {
        $('#suspensionRequestTable').find('a.green.modal-ajax_${suspensionExtensionRequest?.suspensionRequest?.id}').click();
    }
</script>