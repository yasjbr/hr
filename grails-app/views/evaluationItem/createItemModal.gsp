<el:validatableModalForm title="${message(code: 'evaluationItem.label')}"
                         width="50%"
                         name="evaluationItemForm"
                         controller="evaluationItem"
                         hideCancel="true"
                         hideClose="true"
                         callBackFunction="callBackFunction"
                         action="save">
    <msg:modal/>


    <el:hiddenField name="evaluationSection.id" value="${evaluationSectionId}" />

    <g:render template="form" model="[evaluationItem:evaluationItem, hideSection:true]" />

    <el:row/>

    <el:formButton functionName="save" withClose="true" isSubmit="true"/>

    <el:formButton functionName="close"
                   onClick="closeItemModal()"/>

</el:validatableModalForm>

<script>
    function closeItemModal() {
        $('#application-modal-main-content').modal("hide");
    }
    function callBackFunction(json) {
        _dataTables['evaluationItemTable'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>