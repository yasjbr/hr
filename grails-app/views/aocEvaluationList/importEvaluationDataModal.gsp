
<el:modal isModalWithDiv="true" id="createRequestModal" title="${message(code: 'aocListRecord.createRequest.label', args: [requestEntityName])}"
          hideCancel="true" preventCloseOutSide="true" width="70%">
    <msg:modal/>

<el:validatableResetForm name="importEvaluationData" controller="aocEvaluationList" action="importEvaluationData"
                         callBackFunction="drawDataTable">
    <el:hiddenField id="aocCorrespondenceList.id" name="aocCorrespondenceList.id" value="${aocCorrespondenceList?.id}"/>
    <el:hiddenField id="correspondenceType" name="correspondenceType" value="${aocCorrespondenceList?.correspondenceType}"/>
    <el:formGroup>
          <el:fileInput label="${message(code: 'aocCorrespondenceList.importEvaluationData.label', default: 'importEvaluationData')}" id="excelFile" name="excelFile" size="6" class=" isRequired"/>
    </el:formGroup>
    <el:formButton functionName="save" message="${message(code: 'aocCorrespondenceList.upload.label', default: 'upload')}" withClose="true" isSubmit="true"/>
</el:validatableResetForm>

</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    function drawDataTable() {
        _dataTables['listRecordTableInAocList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>