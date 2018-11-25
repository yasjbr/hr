<el:modal isModalWithDiv="true" id="promotionRequestModal"
          title="${message(code: 'promotionList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'promotionRequest.entities', default: 'Request List')}" />

    <lay:collapseWidget id="promotionRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="promotionRequestTableSearchForm">
                <g:render template="/request/search" model="[requestTypeList:requestTypeList]"/>
                <el:formButton functionName="search" onClick="_dataTables['promotionRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('promotionRequestTableSearchForm');_dataTables['promotionRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="promotionRequestTable" searchFormName="promotionRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="request"
                  spaceBefore="true" hasRow="true" action="filterPromotionRequest"
                  serviceName="request" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addPromotionRequestToListForm"
            controller="promotionList"
            action="addPromotionRequestToList">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="promotionListId" value="${promotionList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['promotionRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['promotionListEmployeeTableInPromotionList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
