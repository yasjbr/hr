<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<el:modal isModalWithDiv="true" id="employeeModal"
          title="${message(code: 'generalList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}"/>

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="employeeTableSearchForm">
                <g:render template="/employee/searchInModal" model="[searchForList:true,generalList:generalList]"/>
                <el:formButton functionName="search" onClick="_dataTables['employeeTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('employeeTableSearchForm');_dataTables['employeeTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="employeeTable" searchFormName="employeeTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="employee"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="employee" viewExtendButtons="false"
                  domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="generalList"
            action="addRequestToList">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="generalListId" value="${generalList?.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class=""
                       onclick="callBackBeforeSendFunction()"/>

        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['employeeTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['employeeTableInGeneralList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
