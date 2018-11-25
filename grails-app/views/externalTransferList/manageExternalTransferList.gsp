<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'externalTransferRequest.entities', default: 'externalTransferRequest List')}"/>
    <g:set var="entity"
           value="${message(code: 'externalTransferListEmployee.entity', default: 'externalTransferRequest')}"/>
    <g:set var="title" value="${entity}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'externalTransferList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${externalTransferList?.code}" type="String"
                     label="${message(code: 'externalTransferList.code.label', default: 'code')}"/>
    <lay:showElement value="${externalTransferList?.name}" type="String"
                     label="${message(code: 'externalTransferList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${externalTransferList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'externalTransferList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${externalTransferList?.receivingParty}" type="enum"
                     label="${message(code: 'externalTransferList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>

%{--search for externalTransfer list by externalTransferRequest--}%
<lay:collapseWidget id="externalTransferRequestCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="acceptExternalTransferListEmployeeForm"
                 id="acceptExternalTransferListEmployeeForm">
            <el:hiddenField name="externalTransferList.id" value="${externalTransferList?.id}"/>
            <g:render template="/externalTransferListEmployee/search"
                      model="[:]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['externalTransferRequestTableInExternalTransferList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('acceptExternalTransferListEmployeeForm');_dataTables['externalTransferRequestTableInExternalTransferList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>





<form id="externalTransferRequestSearchForm">
    <el:hiddenField id="externalTransferList.id" name="externalTransferList.id" value="${externalTransferList?.id}"/>

    <el:dataTable id="externalTransferRequestTableInExternalTransferList"
                  searchFormName="acceptExternalTransferListEmployeeForm"
                  dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
                  hasCheckbox="true" widthClass="col-sm-12"
                  controller="externalTransferListEmployee"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter" rowCallbackFunction="manageRow"
                  serviceName="externalTransferListEmployee" domainColumns="DOMAIN_COLUMNS">

        <el:dataTableAction functionName="showRequest" type="function"
                            accessUrl="${createLink(controller: 'externalTransferRequest', action: 'show')}"
                            class="green icon-eye" actionParams="['externalTransferRequest.encodedId']"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show externalTransferRequest')}"/>




        <el:dataTableAction controller="externalTransferListEmployee" action="delete"
                            showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                            actionParams="encodedId"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete externalTransferList')}"/>


    %{--show modal with note details--}%
        <el:dataTableAction
                preventCloseOutSide="true"
                actionParams="id" controller="externalTransferList" action="noteList"
                class="black icon-info-4" type="modal-ajax"
                message="${message(code: 'dispatchList.noteList.label')}"/>
    </el:dataTable>
</form>
<br/><br/>


<div class="clearfix form-actions text-center" style="background:gainsboro;">

%{--to add externalTransferRequest to list--}%
    <g:if test="${externalTransferList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">

        <el:modalLink
                link="${createLink(controller: 'externalTransferList', action: 'addExternalTransferRequestsModal', id: externalTransferList?.id)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon fa fa-plus"></i>${message(code: 'externalTransferList.externalTransferRequests.label')}
        </el:modalLink>
    </g:if>



%{--in case: list status CREATED, we add addExceptional   button --}%
%{--<g:if test="${externalTransferList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">--}%
%{--<el:modalLink id="addExceptionalButton" preventCloseOutSide="true" class=" btn btn-sm btn-danger"--}%
%{--width="70%"--}%
%{--link="${createLink(controller: 'externalTransferList', action: 'addExceptionalModal', id: externalTransferList?.id)}"--}%
%{--label="${message(code: 'list.addExceptional.label')}">--}%
%{--<i class="icon-plus"></i>--}%
%{--</el:modalLink>--}%
%{--</g:if>--}%



%{--in case: list status CREATED, we add send button --}%
    <g:if test="${externalTransferList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink id="sendListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'externalTransferList', action: 'sendDataModal', id: externalTransferList?.id)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>




%{--in case: list status SUBMITTED, we add receive  button --}%
    <g:if test="${showReceiveList}">
        <el:modalLink id="receiveListButton" preventCloseOutSide="true" class="btn btn-sm btn-primary"
                      link="${createLink(controller: 'externalTransferList', action: 'receiveDataModal', id: externalTransferList?.id)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>





%{--in case: list status is RECEIVED, we add accept & reject button--}%
    <g:if test="${externalTransferList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="#"
                      label="${message(code: 'externalTransferList.acceptButton.label')}" style="display: none;"
                      id="hiddenApproveLink">
            <i class="icon-check"></i>
        </el:modalLink>
        <button class=" btn btn-sm btn-approve" onclick="viewApproveModal()">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </button>

        <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info width-135"
                      link="#"
                      label="${message(code: 'externalTransferList.rejectButton.label')}" style="display: none;"
                      id="hiddenRejectLink">
            <i class="icon-cancel"></i>
        </el:modalLink>
        <button class=" btn btn-sm btn-danger" onclick="viewRejectModal()">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </button>

        <el:modalLink id="closeExternalTransferButton" preventCloseOutSide="true"
                      class="btn btn-sm btn-info width-135"
                      link="${createLink(controller: 'externalTransferList', action: 'closeModal', id: externalTransferList?.id)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>








%{--add attachment button --}%
    <btn:attachmentButton onClick="openAttachmentModal('${externalTransferList?.id}')"/>


    <report:staticViewList fileName="correspondenceTemplate"
                           withDataTable="externalTransferRequestTableInExternalTransferList"
                           searchFromName="acceptExternalTransferListEmployeeForm"
                           domain="externalTransferListEmployee" method="getReportData"
                           columns="DOMAIN_COLUMNS" format="pdf"
                           title="${message(code: 'externalTransferList.entity')}"/>

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
        var arrayLength = _dataTablesCheckBoxValues['externalTransferRequestTableInExternalTransferList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'externalTransferList', action: 'approveRequestModal', id: externalTransferList?.id)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['externalTransferRequestTableInExternalTransferList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'externalTransferList', action: 'rejectRequestModal', id: externalTransferList?.id)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${externalTransferList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
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


