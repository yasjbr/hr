<script type="text/javascript">
    function callBackSuccess(json) {
        $('#suspensionRequestTable').find('a.pink.modal-ajax_${suspensionExtensionRequest?.suspensionRequest?.id}').click();

    }
</script>

<g:set var="suspensionRequestFormEntity"
       value="${message(code: 'suspensionExtensionRequest.entity', default: 'suspensionExtensionRequest')}"/>
<g:set var="suspensionRequestFormTitle"
       value="${message(code: 'default.show.label', args: [suspensionRequestFormEntity], default: 'suspensionExtensionRequest')}"/>

<el:validatableModalForm title="${suspensionRequestFormTitle}" name="suspensionExtensionRequestForm" width="70%">
    <msg:modal/>

    <g:render template="/suspensionExtensionRequest/show"
              model="[suspensionExtensionRequest: suspensionExtensionRequest]"/>

    <el:row/>

    <g:if test="${!hideBacak}">
        <el:formButton onClick="closeForm()" functionName="back"/>
    </g:if>

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