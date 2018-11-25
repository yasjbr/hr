<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'vacationRequest.entities', default: 'vacationRequest List')}"/>
    <g:set var="entity" value="${message(code: 'vacationRequest.entity', default: 'vacationRequest')}"/>
    <g:set var="title" value="${message(code: 'default.vacationList.label', args: [entities])}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'vacationList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${vacationList?.code}" type="String"
                     label="${message(code: 'vacationList.code.label', default: 'code')}"/>
    <lay:showElement value="${vacationList?.name}" type="String"
                     label="${message(code: 'vacationList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${vacationList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'vacationList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${vacationList?.receivingParty}" type="enum"
                     label="${message(code: 'vacationList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>

%{--search for vacation list by vacationRequest--}%
<lay:collapseWidget id="vacationRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="acceptVacationListEmployeeForm"
                 id="acceptVacationListEmployeeForm">
            <el:hiddenField name="vacationList.id" value="${vacationList?.id}"/>
            <g:render template="/vacationRequest/search"
                      model="[searchForList: true]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['vacationRequestTableInVacationList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('acceptVacationListEmployeeForm');_dataTables['vacationRequestTableInVacationList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>





<form id="vacationRequestSearchForm">
    <el:hiddenField id="vacationList.id" name="vacationList.id" value="${vacationList?.id}"/>

    <el:dataTable id="vacationRequestTableInVacationList" searchFormName="acceptVacationListEmployeeForm"
                  dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
                  hasCheckbox="true" widthClass="col-sm-12"
                  controller="vacationListEmployee"
                  spaceBefore="true"
                  hasRow="true" rowCallbackFunction="manageRow"
                  action="filter"
                  serviceName="vacationListEmployee" domainColumns="DOMAIN_COLUMNS">

        <el:dataTableAction controller="vacationListEmployee" action="delete"
                            showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                            actionParams="encodedId"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete vacationList')}"/>


    %{--show modal with note details--}%
        <el:dataTableAction
                preventCloseOutSide="true"
                actionParams="id" controller="vacationList" action="noteList"
                class="black icon-info-4" type="modal-ajax"
                message="${message(code: 'dispatchList.noteList.label')}"/>
    </el:dataTable>
</form>
<br/><br/>


<div class="clearfix form-actions text-center" style="background:gainsboro;">

%{--to add vacationRequest to list--}%
    <g:if test="${vacationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">

        <el:modalLink
                link="${createLink(controller: 'vacationList', action: 'addVacationRequestsModal', id: vacationList?.id)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon fa fa-plus"></i>${message(code: 'vacationList.addVacationRequest.label')}
        </el:modalLink>
    </g:if>





%{--in case: list status CREATED, we add send button --}%
    <g:if test="${vacationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink id="sendListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'vacationList', action: 'sendDataModal', id: vacationList?.id)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>




%{--in case: list status SUBMITTED, we add receive  button --}%
    <g:if test="${showReceiveList}">
        <el:modalLink id="receiveListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'vacationList', action: 'receiveDataModal', id: vacationList?.id)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>





%{--in case: list status is RECEIVED, we add accept & reject button--}%
    <g:if test="${vacationList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="#"
                      label="${message(code: 'vacationList.acceptButton.label')}" style="display: none;"
                      id="hiddenApproveLink">
            <i class="icon-check"></i>
        </el:modalLink>
        <button class=" btn btn-sm btn-approve" onclick="viewApproveModal()">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </button>

        <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="#"
                      label="${message(code: 'vacationList.rejectButton.label')}" style="display: none;"
                      id="hiddenRejectLink">
            <i class="icon-cancel"></i>
        </el:modalLink>
        <button class=" btn btn-sm btn-danger" onclick="viewRejectModal()">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </button>

        <el:modalLink id="closeVacationButton" preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="${createLink(controller: 'vacationList', action: 'closeModal', id: vacationList?.id)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>


%{--add attachment button --}%
    <btn:attachmentButton onClick="openAttachmentModal('${vacationList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate" withDataTable="vacationRequestTableInVacationList"
                           searchFromName="acceptVacationListEmployeeForm"
                           domain="vacationListEmployee" method="getReportData"
                           columns="DOMAIN_COLUMNS" format="pdf" title="${message(code: 'vacationList.entity')}"/>

</div>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>

    var testRowList = [];
    var counter = 0;


    // view approvel modal only when a request is selected
    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['vacationRequestTableInVacationList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'vacationList', action: 'approveRequestModal', id: vacationList?.id)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['vacationRequestTableInVacationList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'vacationList', action: 'rejectRequestModal', id: vacationList?.id)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${vacationList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
        var booleanRowStatus = testRow.recordStatus != "${g.message(code: 'EnumListRecordStatus.NEW')}";
        if (booleanListStatus && booleanRowStatus) {
            row.deleteCell(0);
            var x = row.insertCell(0);
            x.innerHTML = " ";
        }
    }
</script>
</body>
</html>


