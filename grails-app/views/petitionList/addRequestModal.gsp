<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestStatus" %>
<el:modal isModalWithDiv="true" id="petitionRequestModal"
          title="${message(code: 'petitionList.label')}"
          hideCancel="true"
          preventCloseOutSide="true" width="70%">
    <msg:modal/>
    <msg:page/>

    <g:set var="requestSearchTitle" value="${message(code: 'request.entities', default: 'Request List')}" />

    <lay:collapseWidget id="petitionRequestCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [requestSearchTitle])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="petitionRequestTableSearchForm">
                %{--isList flag is used in search form to not show the request status field--}%
                <g:render template="/petitionRequest/search" model="[isList:true]"/>
                <el:hiddenField name="requestStatus" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED_BY_WORKFLOW}" />
                <el:formButton functionName="search" onClick="_dataTables['petitionRequestTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('petitionRequestTableSearchForm');_dataTables['petitionRequestTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="petitionRequestTable" searchFormName="petitionRequestTableSearchForm"
                  hasCheckbox="true" widthClass="col-sm-12" controller="petitionRequest"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="petitionRequest" viewExtendButtons="false" domainColumns="DOMAIN_COLUMNS">
    </el:dataTable>

    <el:validatableForm
            callBackFunction="callBackFunction"
            name="addRequestToListForm"
            controller="petitionList"
            action="addRequestToList">

        <el:hiddenField name="checked_requestIdsList" value=""/>
        <el:hiddenField name="petitionListId" value="${petitionList.id}"/>
        <el:formButton functionName="addButton" isSubmit="true" withClose="true" class="" onclick="callBackBeforeSendFunction()"/>
    </el:validatableForm>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));

    function callBackBeforeSendFunction() {
        $("#checked_requestIdsList").val(_dataTablesCheckBoxValues['petitionRequestTable']);
        return true;
    }

    function callBackFunction(json) {
        _dataTables['petitionRequestTableInPetitionList'].draw();
        $('#application-modal-main-content').modal("hide");
    }
</script>
