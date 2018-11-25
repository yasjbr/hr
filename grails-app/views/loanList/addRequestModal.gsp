<el:modal isModalWithDiv="true" id="loanRequestModal"
          title="${message(code: 'loanList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="90%">
    <msg:modal/>
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}" />

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="loanRequestTableSearchForm">
                <g:render template="/loanRequest/search" model="[hideStatusSearch:true]"/>
                <el:hiddenField name="requestStatusHidden" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW.toString()}"/>
                <el:formButton functionName="search" onClick="_dataTables['loanRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('loanRequestTableSearchForm');_dataTables['loanRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="loanRequestTable" searchFormName="loanRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="loanRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="loanRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="loanList"
            action="addRequest">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="loanListId" value="${loanList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['loanRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['loanListPersonTable'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
