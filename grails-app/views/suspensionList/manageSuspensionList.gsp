<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'suspensionRequest.entities', default: 'suspensionRequest List')}"/>
    <g:set var="entity" value="${message(code: 'suspensionRequest.entity', default: 'suspensionRequest')}"/>
    <g:set var="title" value="${message(code: 'default.suspensionList.label', args: [entities])}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'suspensionList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${suspensionList?.code}" type="String"
                     label="${message(code: 'suspensionList.code.label', default: 'code')}"/>
    <lay:showElement value="${suspensionList?.name}" type="String"
                     label="${message(code: 'suspensionList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${suspensionList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'suspensionList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${suspensionList?.receivingParty}" type="enum"
                     label="${message(code: 'suspensionList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>

%{--search for suspension list by suspensionRequest--}%
<lay:collapseWidget id="suspensionRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="acceptSuspensionListEmployeeForm"
                 id="acceptSuspensionListEmployeeForm">
            <el:hiddenField name="suspensionList.id" value="${suspensionList?.id}"/>
            <g:render template="/suspensionListEmployee/search"
                      model="[:]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['suspensionRequestTableInSuspensionList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('acceptSuspensionListEmployeeForm');_dataTables['suspensionRequestTableInSuspensionList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>





<form id="suspensionRequestSearchForm">
    <el:hiddenField id="suspensionList.id" name="suspensionList.id" value="${suspensionList?.id}"/>

    <el:dataTable id="suspensionRequestTableInSuspensionList" searchFormName="acceptSuspensionListEmployeeForm"
                  dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
                  hasCheckbox="true" widthClass="col-sm-12"
                  controller="suspensionListEmployee"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="suspensionListEmployee" rowCallbackFunction="manageRow"
                  domainColumns="DOMAIN_COLUMNS">

        <el:dataTableAction controller="suspensionListEmployee" action="delete"
                            showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                            actionParams="encodedId"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete suspensionList')}"/>


    %{--show modal with note details--}%
        <el:dataTableAction
                preventCloseOutSide="true"
                actionParams="id" controller="suspensionList" action="noteList"
                class="black icon-info-4" type="modal-ajax"
                message="${message(code: 'dispatchList.noteList.label')}"/>
    </el:dataTable>
</form>
<br/><br/>


<div class="clearfix form-actions text-center" style="background:gainsboro;">

%{--to add suspensionRequest to list--}%
    <g:if test="${suspensionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">

        <el:modalLink
                link="${createLink(controller: 'suspensionList', action: 'addSuspensionRequestsModal', id: suspensionList?.id)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon fa fa-plus"></i>${message(code: 'suspensionList.addSuspensionRequest.label')}
        </el:modalLink>
    </g:if>





%{--in case: list status CREATED, we add send button --}%
    <g:if test="${suspensionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink id="sendListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'suspensionList', action: 'sendDataModal', id: suspensionList?.id)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>




%{--in case: list status SUBMITTED, we add receive  button --}%
    <g:if test="${showReceiveList}">
        <el:modalLink id="receiveListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'suspensionList', action: 'receiveDataModal', id: suspensionList?.id)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>





%{--in case: list status is RECEIVED, we add accept & reject button--}%
    <g:if test="${suspensionList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="#"
                      label="${message(code: 'suspensionList.acceptButton.label')}" style="display: none;"
                      id="hiddenApproveLink">
            <i class="icon-check"></i>
        </el:modalLink>
        <button class=" btn btn-sm btn-approve" onclick="viewApproveModal()">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </button>

        <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="#"
                      label="${message(code: 'suspensionList.rejectButton.label')}" style="display: none;"
                      id="hiddenRejectLink">
            <i class="icon-cancel"></i>
        </el:modalLink>
        <button class=" btn btn-sm btn-danger" onclick="viewRejectModal()">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </button>

        <el:modalLink id="closeSuspensionButton" preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="${createLink(controller: 'suspensionList', action: 'closeModal', id: suspensionList?.id)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>

%{--add attachment button --}%
    <btn:attachmentButton onClick="openAttachmentModal('${suspensionList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate" withDataTable="suspensionRequestTableInSuspensionList"
                           searchFromName="acceptSuspensionListEmployeeForm"
                           domain="suspensionListEmployee" method="getReportData"
                           columns="DOMAIN_COLUMNS" format="pdf" title="${message(code: 'suspensionList.entity')}"/>

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
        var arrayLength = _dataTablesCheckBoxValues['suspensionRequestTableInSuspensionList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'suspensionList', action: 'approveRequestModal', id: suspensionList?.id)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['suspensionRequestTableInSuspensionList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'suspensionList', action: 'rejectRequestModal', id: suspensionList?.id)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${suspensionList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
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


