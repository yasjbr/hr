<g:if test="${serviceList.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE}">
    <g:set var="entities"
           value="${message(code: 'recallToService.entities', default: 'recallToService List')}"/>
    <g:set var="entity" value="${message(code: 'recallToService.entity', default: 'recallToService List')}"/>
    <g:set var="title" value="${message(code: 'recallToService.label', args: [entities])}"/>
</g:if>
<g:else>
    <g:set var="entities"
           value="${message(code: 'endOfService.entities', default: 'endOfService List')}"/>
    <g:set var="entity" value="${message(code: 'endOfService.entity', default: 'endOfService List')}"/>
    <g:set var="title" value="${message(code: 'endOfService.label', args: [entities])}"/>
</g:else>

<el:modal isModalWithDiv="true" id="eligibleRequestModal"
          title="${title}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="employmentServiceRequestSearchTitle" value="${entities}"/>

    <lay:collapseWidget id="periodSettlementRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [employmentServiceRequestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="eligibleRequestTableSearchForm">
                <el:hiddenField name="filter_requestStatus"
                                value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}"/>
                <g:if test="${serviceList.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE}">
                    <el:hiddenField name="requestType"
                                    value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.RETURN_TO_SERVICE}"/>
                </g:if><g:else>
                <el:hiddenField name="requestType"
                                value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.END_OF_SERVICE}"/>
            </g:else>
                <el:hiddenField name="isListModal" value="true"/>
                <g:render template="/employmentServiceRequest/search" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['eligibleRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('eligibleRequestTableSearchForm');_dataTables['eligibleRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="eligibleRequestTable"
                  searchFormName="eligibleRequestTableSearchForm"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="employmentServiceRequest"
                  action="filter"
                  spaceBefore="true"
                  hasRow="true"
                  viewExtendButtons="false"
                  domainColumns="DOMAIN_COLUMNS"
                  serviceName="employmentServiceRequest">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addEligibleRequestToListForm"
            controller="serviceList"
            action="addEmploymentServiceRequestToList">
        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="serviceListId" value="${serviceList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class=""
                       onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['eligibleRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['employmentServiceTableInServiceList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
