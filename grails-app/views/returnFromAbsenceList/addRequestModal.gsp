<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<el:modal isModalWithDiv="true" id="returnFromAbsenceRequestModal"
          title="${message(code: 'returnFromAbsenceList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}" />

    <lay:collapseWidget id="returnFromAbsenceRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="returnFromAbsenceRequestTableSearchForm">
                %{--isList flag is used in search form to not show the request status field--}%
                <g:render template="/returnFromAbsenceRequest/search" model="[isList:true]"/>
                <el:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}" />
                <el:formButton functionName="search" onClick="_dataTables['returnFromAbsenceRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('returnFromAbsenceRequestTableSearchForm');_dataTables['returnFromAbsenceRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="returnFromAbsenceRequestTable" searchFormName="returnFromAbsenceRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="returnFromAbsenceRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="returnFromAbsenceRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="returnFromAbsenceList"
            action="addRequestToList">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="returnFromAbsenceListId" value="${returnFromAbsenceList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['returnFromAbsenceRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['returnFromAbsenceRequestTableInReturnFromAbsenceList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
