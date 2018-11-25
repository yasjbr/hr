<el:modal isModalWithDiv="true" id="externalTransferRequestModal"
          title="${message(code: 'externalTransferList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}"/>

    <lay:collapseWidget id="externalTransferRequestCollapseWidgetForAddRequest" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="externalTransferRequestTableSearchForm">
                <g:render template="/externalTransferRequest/search" model="[searchForList: true]"/>
                <el:formButton functionName="search" onClick="_dataTables['externalTransferRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('externalTransferRequestTableSearchForm');_dataTables['externalTransferRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="externalTransferRequestTable" searchFormName="externalTransferRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="externalTransferRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="externalTransferRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addExternalTransferRequestToListForm"
            controller="externalTransferList"
            action="addExternalTransferRequests">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="id" value="${id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class=""
                       onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['externalTransferRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['externalTransferRequestTableInExternalTransferList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
