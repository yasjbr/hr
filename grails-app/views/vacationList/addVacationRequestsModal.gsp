<el:modal isModalWithDiv="true" id="vacationRequestModal"
          title="${message(code: 'vacationList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}"/>

    <lay:collapseWidget id="vacationRequestModalCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="vacationRequestTableSearchForm">
                <g:render template="/vacationRequest/searchForList" model="[searchForList: true]"/>
                <el:formButton functionName="search" onClick="_dataTables['vacationRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('vacationRequestTableSearchForm');_dataTables['vacationRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="vacationRequestTable" searchFormName="vacationRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="vacationRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="vacationRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addVacationRequestToListForm"
            controller="vacationList"
            action="addVacationRequests">

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
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['vacationRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['vacationRequestTableInVacationList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
