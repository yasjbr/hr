<el:modal isModalWithDiv="true" id="listRequestModal" title="${message(code: 'aocListRecord.addRequest.label', args: [requestEntityName])}"
          hideCancel="true" preventCloseOutSide="true" width="70%">
    <msg:page/>

    <el:form action="#" name="listRequestTableSearchForm">
        <g:hiddenField name="aocCorrespondenceList.id" value="${aocCorrespondenceList?.id}" />
        <g:hiddenField name="correspondenceType" value="${aocCorrespondenceList?.correspondenceType}" />
        <g:hiddenField name="addedToAocList" value="false" />
        <g:if test="${firmId}">
            <g:hiddenField name="firm.id" value="${firmId}"/>
        </g:if>
        <g:else>
            <el:formGroup>
                <el:select id="selectFirmId" from="${firms}" size="6" optionKey="id" optionValue="name" class=" isRequired"
                           onChange="handleFirmChange()" name="firm.id" label="${message(code: 'firm.label', default: 'firm')}"/>
            </el:formGroup>
        </g:else>
    </el:form>

    <el:dataTable id="listRequestTable" searchFormName="listRequestTableSearchForm" hasCheckbox="true" action="filterNotIncludedRecords"
                  controller="aocListRecord" spaceBefore="true" hasRow="true" widthClass="col-sm-12" messagePrefix="${messagePrefix}"
                  viewExtendButtons="false" serviceName="${serviceName}" domainColumns="hrDomainColumns">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction" name="addRequestToListForm" controller="aocListRecord" action="saveExistingRecords">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="aocCorrespondenceList.id" value="${aocCorrespondenceList?.id}"/>
        <el:hiddenField name="correspondenceType" value="${aocCorrespondenceList?.correspondenceType}" />
        <g:hiddenField name="firmId" value="${firmId}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class=""
                       onclick="callBackBeforeSendFunction()"/>

        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['listRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['listRecordTableInAocList'].draw();
        $('#application-modal-main-content').modal("hide");
    }

    /**
     * reload requests table after selecting firm
     */
    function handleFirmChange(){
        _dataTables['listRequestTable'].draw();
        $('#firmId').val($('#selectFirmId').val());
    }
</script>
