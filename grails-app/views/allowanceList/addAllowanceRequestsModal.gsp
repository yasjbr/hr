<el:modal isModalWithDiv="true" id="allowanceRequestModal"
          title="${message(code: 'allowanceList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}"/>

    <lay:collapseWidget id="allowanceRequestModalCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="allowanceRequestTableSearchForm">
                <g:render template="/allowanceRequest/searchForList" model="[searchForList: true]"/>
                <el:formButton functionName="search" onClick="_dataTables['allowanceRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('allowanceRequestTableSearchForm');_dataTables['allowanceRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="allowanceRequestTable" searchFormName="allowanceRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="allowanceRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="allowanceRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addAllowanceRequestToListForm"
            controller="allowanceList"
            action="addAllowanceRequests">

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
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['allowanceRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['allowanceRequestTableInAllowanceList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
