<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<el:modal isModalWithDiv="true" id="maritalStatusRequestModal"
          title="${message(code: 'maritalStatusList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}" />

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="maritalStatusRequestTableSearchForm">
                <g:render template="/maritalStatusRequest/search" model="[isList:true]"/>
                <el:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}" />
                <el:formButton functionName="search" onClick="_dataTables['maritalStatusRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('maritalStatusRequestTableSearchForm');_dataTables['maritalStatusRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="maritalStatusRequestTable" searchFormName="maritalStatusRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="maritalStatusRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="maritalStatusRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="maritalStatusList"
            action="addRequestToList">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="maritalStatusListId" value="${maritalStatusList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['maritalStatusRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['maritalStatusRequestTableInMaritalStatusList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
