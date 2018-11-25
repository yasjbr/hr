<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<el:modal isModalWithDiv="true" id="applicantModal"
          title="${message(code: 'applicantInspectionResultList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}"/>

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="applicantTableSearchForm">
                <g:render template="/applicant/searchForList" model="[searchForList:true,applicantInspectionResultList:applicantInspectionResultList]"/>
                <el:formButton functionName="search" onClick="_dataTables['applicantTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('applicantTableSearchForm');_dataTables['applicantTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="applicantTable" searchFormName="applicantTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="applicant"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="applicant" viewExtendButtons="false"
                  domainColumns="LIST_DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="applicantInspectionResultList"
            action="addRequestToList">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="applicantInspectionResultListId" value="${applicantInspectionResultList?.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class=""
                       onclick="callBackBeforeSendFunction()"/>

        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['applicantTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['applicantTableInApplicantInspectionResultList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
