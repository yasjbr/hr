<el:modal isModalWithDiv="true" id="suspensionRequestModal"
          title="${message(code: 'suspensionList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}"/>

    <lay:collapseWidget id="suspensionRequestModalCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="suspensionRequestTableSearchForm">
                <g:render template="/suspensionRequest/search" model="[searchForList: true]"/>
                <el:formButton functionName="search" onClick="_dataTables['suspensionRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('suspensionRequestTableSearchForm');_dataTables['suspensionRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="suspensionRequestTable" searchFormName="suspensionRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="suspensionRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="suspensionRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addSuspensionRequestToListForm"
            controller="suspensionList"
            action="addSuspensionRequests">

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
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['suspensionRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['suspensionRequestTableInSuspensionList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
