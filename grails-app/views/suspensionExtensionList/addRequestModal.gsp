<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<el:modal isModalWithDiv="true" id="suspensionExtensionRequestModal"
          title="${message(code: 'suspensionExtensionList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}"/>

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="suspensionExtensionRequestTableSearchForm">
                <g:render template="/suspensionExtensionRequest/searchModal" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['suspensionExtensionRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('suspensionExtensionRequestTableSearchForm');_dataTables['suspensionExtensionRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="suspensionExtensionRequestTable" searchFormName="suspensionExtensionRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="suspensionExtensionRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="suspensionExtensionRequest" viewExtendButtons="false"
                  domainColumns="LIST_DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="suspensionExtensionList"
            action="addRequestToList">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="suspensionExtensionListId" value="${suspensionExtensionList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class=""
                       onclick="callBackBeforeSendFunction()"/>

        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['suspensionExtensionRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['suspensionExtensionRequestTableInSuspensionExtensionList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
