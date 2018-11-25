<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<el:modal isModalWithDiv="true" id="employeeEvaluationModal"
          title="${message(code: 'evaluationList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}" />

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="employeeEvaluationTableSearchForm">
                <g:render template="/employeeEvaluation/search" model="[isList:true]"/>
                <el:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}" />
                <el:formButton functionName="search" onClick="_dataTables['employeeEvaluationTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('employeeEvaluationTableSearchForm');_dataTables['employeeEvaluationTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="employeeEvaluationTable" searchFormName="employeeEvaluationTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="employeeEvaluation"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="employeeEvaluation" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="evaluationList"
            action="addRequestToList">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="evaluationListId" value="${evaluationList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['employeeEvaluationTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['employeeEvaluationTableInEvaluationList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
