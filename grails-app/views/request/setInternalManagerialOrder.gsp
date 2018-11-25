<el:modal isModalWithDiv="true" id="requestOrderInfoModal"
          title="${message(code: 'request.setManagerialOrderInfo.title', args:[requestTypeDescription])}"
          hideCancel="true" preventCloseOutSide="true" width="70%">
    <msg:modal/>

    <el:validatableForm callBackFunction="callBackFunction" name="setInternalOrderForm" controller="request"
            action="saveInternalManagerialOrder">

        <el:hiddenField name="encodedId" value="${requestId}"/>

        <g:render template="wrapperManagerialOrder" model="[colSize:8, hideExternalOrderInfo:true, formName:'setInternalOrderForm']" />

        <el:formButton functionName="save" isSubmit="true" withClose="true" class=""/>
        <el:formButton onClick="closeForm()" functionName="close"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">

    if("${dataTableId}"!=""){
        gui.dataTable.initialize($('#application-modal-main-content'));

        function callBackFunction(json) {
            _dataTables["${dataTableId}"].draw();
            $('#application-modal-main-content').modal("hide");
        }
    }

    function closeForm() {
        $('#application-modal-main-content').modal("hide");
    }

</script>
