<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'loanNominatedEmployee.entities', default: 'loanNominatedEmployee List')}"/>
    <g:set var="entity" value="${message(code: 'loanNominatedEmployee.entity', default: 'loan list')}"/>
    <g:set var="entities" value="${message(code: 'loanNominatedEmployee.entities', default: 'loan list')}"/>
    <g:set var="title" value="${message(code: 'loanNoticeReplayList.manage.label')}"/>
    <title>${title}</title>

</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'loanNoticeReplayList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${loanNoticeReplayList?.code}" type="String"
                     label="${message(code: 'loanNoticeReplayList.code.label', default: 'code')}"/>
    <lay:showElement value="${loanNoticeReplayList?.name}" type="String"
                     label="${message(code: 'loanNoticeReplayList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${loanNoticeReplayList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'loanNoticeReplayList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${loanNoticeReplayList?.receivingParty}" type="enum"
                     label="${message(code: 'loanNoticeReplayList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>

<lay:collapseWidget id="loanCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="loanNominatedEmployeeSearchForm"
                 id="loanNominatedEmployeeSearchForm">
            <el:hiddenField id="loanNoticeReplayList.id" name="loanNoticeReplayList.id"
                            value="${loanNoticeReplayList?.id}"/>
            <g:render template="/loanNominatedEmployee/search" model="[namePrefix: 'loanRequest']"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['loanNominatedEmployeeTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('loanNominatedEmployeeSearchForm');_dataTables['loanNominatedEmployeeTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>

<el:dataTable id="loanNominatedEmployeeTable" searchFormName="loanNominatedEmployeeSearchForm"
              dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanNominatedEmployee" spaceBefore="true"
              hasRow="true"
              action="filter" serviceName="loanNominatedEmployee" rowCallbackFunction="manageRow">

    <el:dataTableAction functionName="showRequest" type="function"
                        accessUrl="${createLink(controller: 'loanNoticeReplayRequest', action: 'show')}"
                        class="green icon-eye" actionParams="['loanNoticeReplayRequest.encodedId']"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show loanNoticeReplayRequest')}"/>


%{--show modal with note details--}%
    <el:dataTableAction controller="loanNominatedEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete loan')}"/>

    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="encodedId" controller="loanNoticeReplayList" action="noteList"
            class="black icon-info-4" type="modal-ajax"
            message="${message(code: 'loanNoticeReplayList.noteList.label')}"/>

</el:dataTable>

<el:row/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${loanNoticeReplayList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'loanNoticeReplayList', action: 'addRequestModal', id: loanNoticeReplayList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary" label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${loanNoticeReplayList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'loanNoticeReplayList', action: 'sendListModal', id: loanNoticeReplayList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">

        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'loanNoticeReplayList', action: 'receiveListModal', id: loanNoticeReplayList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>


    <g:if test="${loanNoticeReplayList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-approve"
                      link="#"
                      label="" style="display: none;" id="hiddenApproveLink">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </el:modalLink>
        <button class=" btn btn-sm btn-approve" onclick="viewApproveModal()">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </button>

        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-danger"
                      link="#"
                      label="" style="display: none;" id="hiddenRejectLink">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </el:modalLink>
        <button class=" btn btn-sm btn-danger" onclick="viewRejectModal()">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </button>
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'loanNoticeReplayList', action: 'closeListModal', id: loanNoticeReplayList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>


    <btn:attachmentButton onClick="openAttachmentModal('${loanNoticeReplayList?.id}')"/>


    <report:staticViewList fileName="correspondenceTemplate" withDataTable="loanNominatedEmployeeTable"
                           searchFromName="loanNominatedEmployeeSearchForm"
                           domain="loanNominatedEmployee" method="getReportData"
                           columns="DOMAIN_COLUMNS" format="pdf"
                           title="${message(code: 'loanNoticeReplayList.entity')}"/>

</div>


<g:render template="/attachment/attachmentCreateModal" model="[sharedTemplate: employee.autocomplete()]"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>





<script>

    var testRowList = [];
    var counter = 0;


    function showRequest(row) {
        window.location = "${createLink(controller: 'loanNoticeReplayRequest',action: 'show')}?encodedId=" + row;
    }

    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'shared');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('sharedOperationType', "${sharedOperationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    /*allow delete when recordStatus is NEW  */
    function manageExecuteActions(row) {
        testRowList.push(row);
        if (${loanNoticeReplayList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}) {
            return true;
        }
        return false;
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${loanNoticeReplayList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
        var booleanRowStatus = testRow.loanNoticeReplayRequest.requestStatus != "${g.message(code: 'EnumRequestStatus.SENT_BY_LIST')}";
        if (booleanListStatus && booleanRowStatus) {
            row.deleteCell(0);
            var x = row.insertCell(0);
            x.innerHTML = " ";
        }
    }

    /*the show is needed when the record comes from request */
    function manageShowAction(row) {
        if (row.isRequest == true || row.isRequest == "true") {
            return true;
        }
        return false;
    }

    // view approvel modal only when a request is selected
    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['loanNominatedEmployeeTable'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'loanNoticeReplayList', action: 'approveRequestModal', id: loanNoticeReplayList?.encodedId)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['loanNominatedEmployeeTable'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'loanNoticeReplayList', action: 'rejectRequestModal', id: loanNoticeReplayList?.encodedId)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }
</script>
</body>
</html>


