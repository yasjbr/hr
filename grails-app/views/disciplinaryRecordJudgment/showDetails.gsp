<el:modal isModalWithDiv="true"  id="disciplinaryRecordJudgmentDetailsModal" title="${message(code:'disciplinaryRecordJudgment.showDetails.label')}"
          preventCloseOutSide="true" width="80%">

    <div id="disciplinaryRecordJudgmentDetailsDiv">


    <lay:showWidget size="12" >

        <lay:showElement value="${disciplinaryRecordJudgment?.disciplinaryReasons?.toList()?.join(",")}"
                         type="String" label="${message(code:'disciplinaryRecordJudgment.disciplinaryReasons.label',default:'disciplinaryReasons')}" />

        <lay:showElement value="${disciplinaryRecordJudgment?.value}" type="String"
                         label="${message(code:'disciplinaryRecordJudgment.value.label',default:'value')}" />


        <g:if test="${disciplinaryRecordJudgment?.transientData?.currencyDTO}">
            <lay:showElement value="${disciplinaryRecordJudgment?.transientData?.currencyDTO}" type="String"
                             label="${message(code:'disciplinaryRecordJudgment.currencyId.label',default:'currencyId')}" />

        </g:if>
        <g:else>
            <lay:showElement value="${disciplinaryRecordJudgment?.transientData?.unitDTO}" type="String"
                             label="${message(code:'disciplinaryRecordJudgment.unitId.label',default:'unitId')}" />

        </g:else>


        <lay:showElement value="${disciplinaryRecordJudgment?.fromDate}" type="ZonedDate"
                         label="${message(code:'disciplinaryRecordJudgment.fromDate.label',default:'fromDate')}" />


        <lay:showElement value="${disciplinaryRecordJudgment?.toDate}" type="ZonedDate"
                         label="${message(code:'disciplinaryRecordJudgment.toDate.label',default:'toDate')}" />


        <lay:showElement value="${disciplinaryRecordJudgment?.disciplinaryListNote?.orderNo}" type="String"
                         label="${message(code:'disciplinaryRecordJudgment.orderNo.label',default:'orderNo')}" />


        <lay:showElement value="${disciplinaryRecordJudgment?.disciplinaryListNote?.note}" type="String"
                         label="${message(code:'disciplinaryRecordJudgment.note.label',default:'note')}" />
    </lay:showWidget>

        <el:row/>

    </div>
</el:modal>



<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#disciplinaryRecordJudgmentDetailsDiv').length;
        if (isCreate > 0) {
            $('#addDisciplinaryRecordJudgmentButton').click();
        }
    });

</script>