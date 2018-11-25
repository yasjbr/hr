<el:modal isModalWithDiv="true" id="exceptionalRequestModal"
          title="${message(code: 'endOfService.entities')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="employmentServiceRequestSearchTitle" value="${message(code: 'endOfService.entities', default: 'endOfService List')}" />

    <lay:collapseWidget id="employmentServiceRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [employmentServiceRequestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="exceptionalRequestTableSearchForm">
                <el:hiddenField name="requestType" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.END_OF_SERVICE}" />
                <el:hiddenField name="filter_requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}"/>
                <g:render template="/employmentServiceRequest/search" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['exceptionalRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('exceptionalRequestTableSearchForm');_dataTables['exceptionalRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="exceptionalRequestTable"
                  searchFormName="exceptionalRequestTableSearchForm"
                  dataTableTitle="${title}"
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
            name="addExceptionalToListForm"
            controller="serviceList"
            action="addExceptionalToList">
        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="serviceListId" value="${serviceList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
        <el:formButton functionName="close" onClick="callBackFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['exceptionalRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['employmentServiceTableInServiceList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
