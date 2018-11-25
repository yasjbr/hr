<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'allowanceRequest.entities', default: 'allowanceRequest List')}"/>
    <g:set var="entity" value="${message(code: 'allowanceRequest.entity', default: 'allowanceRequest')}"/>
    <g:set var="title" value="${message(code: 'default.allowanceList.label', args: [entities])}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'allowanceList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${allowanceList?.code}" type="String"
                     label="${message(code: 'allowanceList.code.label', default: 'code')}"/>
    <lay:showElement value="${allowanceList?.name}" type="String"
                     label="${message(code: 'allowanceList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${allowanceList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'allowanceList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${allowanceList?.receivingParty}" type="enum"
                     label="${message(code: 'allowanceList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>

%{--search for allowance list by allowanceRequest--}%
<lay:collapseWidget id="allowanceRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="acceptAllowanceListEmployeeForm"
                 id="acceptAllowanceListEmployeeForm">
            <el:hiddenField name="allowanceList.id" value="${allowanceList?.id}"/>
            <g:render template="/allowanceRequest/search"
                      model="[:]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['allowanceRequestTableInAllowanceList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('acceptAllowanceListEmployeeForm');_dataTables['allowanceRequestTableInAllowanceList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<form id="allowanceRequestSearchForm">
    <el:hiddenField id="allowanceList.id" name="allowanceList.id" value="${allowanceList?.id}"/>

    <el:dataTable id="allowanceRequestTableInAllowanceList" searchFormName="acceptAllowanceListEmployeeForm"
                  dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
                  hasCheckbox="true" widthClass="col-sm-12"
                  controller="allowanceListEmployee"
                  spaceBefore="true"
                  hasRow="true" rowCallbackFunction="manageRow"
                  action="filter"
                  serviceName="allowanceListEmployee" domainColumns="LIST_DOMAIN_COLUMNS">

        <el:dataTableAction controller="allowanceListEmployee" action="delete"
                            showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                            actionParams="encodedId"
                            message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete allowanceList')}"/>


    %{--show modal with note details--}%
        <el:dataTableAction
                preventCloseOutSide="true"
                actionParams="id" controller="allowanceList" action="noteList"
                class="black icon-info-4" type="modal-ajax"
                message="${message(code: 'dispatchList.noteList.label')}"/>
    </el:dataTable>
</form>
<br/><br/>


<div class="clearfix form-actions text-center" style="background:gainsboro;">

%{--to add allowanceRequest to list--}%
    <g:if test="${allowanceList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">

        <el:modalLink
                link="${createLink(controller: 'allowanceList', action: 'addAllowanceRequestsModal', id: allowanceList?.id)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon fa fa-plus"></i>${message(code: 'allowanceList.addAllowanceRequest.label')}
        </el:modalLink>
    </g:if>




%{--in case: list status CREATED, we add send button --}%
    <g:if test="${allowanceList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink id="sendListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'allowanceList', action: 'sendDataModal', id: allowanceList?.id)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>


%{--in case: list status SUBMITTED, we add receive  button --}%
    <g:if test="${showReceiveList}">

        <el:modalLink id="receiveListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'allowanceList', action: 'receiveDataModal', id: allowanceList?.id)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>

%{--in case: list status is RECEIVED, we add accept & reject button--}%
    <g:if test="${allowanceList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">

        <g:if test="${!isCentralizedWithAOC}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-info width-135"
                      link="#"
                      label="${message(code: 'allowanceList.acceptButton.label')}" style="display: none;"
                      id="hiddenApproveLink">
            <i class="icon-check"></i>
        </el:modalLink>
        <button class=" btn btn-sm btn-approve" onclick="viewApproveModal()">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </button>
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-info width-135"
                      link="#"
                      label="${message(code: 'allowanceList.rejectButton.label')}" style="display: none;"
                      id="hiddenRejectLink">
            <i class="icon-cancel"></i>
        </el:modalLink>
        <button class=" btn btn-sm btn-danger" onclick="viewRejectModal()">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </button>
        </g:if>

        <el:modalLink id="closeAllowanceButton" preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="${createLink(controller: 'allowanceList', action: 'closeModal', id: allowanceList?.id)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>

%{--add attachment button --}%
    <btn:attachmentButton onClick="openAttachmentModal('${allowanceList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate" withDataTable="allowanceRequestTableInAllowanceList"
                           searchFromName="acceptAllowanceListEmployeeForm"
                           domain="allowanceListEmployee" method="getReportData"
                           columns="LIST_DOMAIN_COLUMNS" format="pdf" title="${message(code: 'allowanceList.entity')}"/>

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
        var arrayLength = _dataTablesCheckBoxValues['allowanceRequestTableInAllowanceList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'allowanceList', action: 'approveRequestModal', id: allowanceList?.id)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['allowanceRequestTableInAllowanceList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'allowanceList', action: 'rejectRequestModal', id: allowanceList?.id)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${allowanceList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
        var booleanRowStatus = testRow.recordStatus != "${g.message(code: 'EnumListRecordStatus.NEW')}";
        console.log("booleanListStatus = " + booleanListStatus + " , " + (booleanListStatus=="true"));
        console.log("booleanRowStatus = " + booleanRowStatus);
        if (booleanListStatus=="true" && booleanRowStatus) {
            row.deleteCell(0);
            var x = row.insertCell(0);
            x.innerHTML = " ";
        }
    }
</script>
</body>
</html>


