<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'loanRequest.entity', default: 'loan list')}"/>
    <g:set var="entities" value="${message(code: 'loanRequest.entities', default: 'loan list')}"/>
    <g:set var="title" value="${entity}"/>
    <title>${title}</title>

</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'loanList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${loanList?.code}" type="String"
                     label="${message(code: 'loanList.code.label', default: 'code')}"/>
    <lay:showElement value="${loanList?.name}" type="String"
                     label="${message(code: 'loanList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${loanList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'loanList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${loanList?.receivingParty}" type="enum"
                     label="${message(code: 'loanList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>

<lay:collapseWidget id="loanCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="loanListPersonSearchForm"
                 id="loanListPersonSearchForm">
            <el:hiddenField id="loanList.id" name="loanList.id" value="${loanList?.id}"/>
            <g:render template="/loanListPerson/search" model="[namePrefix: 'loanRequest']"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['loanListPersonTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('loanListPersonSearchForm');_dataTables['loanListPersonTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>

<el:dataTable id="loanListPersonTable" searchFormName="loanListPersonSearchForm"
              dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
              hasCheckbox="true" widthClass="col-sm-12" controller="loanListPerson" spaceBefore="true" hasRow="true"
              action="filter" serviceName="loanListPerson" rowCallbackFunction="manageRow">

    <el:dataTableAction functionName="showRequest" type="function"
                        accessUrl="${createLink(controller: 'loanRequest', action: 'show')}"
                        class="green icon-eye" actionParams="['loanRequest.encodedId']"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show externalTransferRequest')}"/>

    <el:dataTableAction controller="loanListPerson" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete loan')}"/>


%{--show modal with note details--}%
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="encodedId" controller="loanList" action="noteList"
            class="black icon-info-4" type="modal-ajax"
            message="${message(code: 'loanList.noteList.label')}"/>

</el:dataTable>

<el:row/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${loanList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'loanList', action: 'addRequestModal', id: loanList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary" label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${loanList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'loanList', action: 'sendListModal', id: loanList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">

        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'loanList', action: 'receiveListModal', id: loanList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${loanList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-approve"
                      link="${createLink(controller: 'loanList', action: 'approveRequestModal', id: loanList?.encodedId)}"
                      label="" style="display: none;" id="hiddenApproveLink">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </el:modalLink>
        <button class=" btn btn-sm btn-approve" onclick="viewApproveModal()">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </button>

        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-default"
                      link="${createLink(controller: 'loanList', action: 'rejectRequestModal', id: loanList?.encodedId)}"
                      label="" style="display: none;" id="hiddenRejectLink">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </el:modalLink>
        <button class=" btn btn-sm btn-danger" onclick="viewRejectModal()">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </button>

        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'loanList', action: 'closeListModal', id: loanList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>

    </g:if>


    <btn:attachmentButton onClick="openAttachmentModal('${loanList?.id}')"/>


    <report:staticViewList fileName="correspondenceTemplate" withDataTable="loanListPersonTable"
                           searchFromName="loanListPersonSearchForm"
                           domain="loanListPerson" method="getReportData"
                           columns="DOMAIN_COLUMNS" format="pdf" title="${message(code: 'loanList.entity')}"/>

</div>


<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>





<script>


    var testRowList = [];
    var counter = 0;

    function showRequest(row) {
        window.location = "${createLink(controller: 'loanRequest',action: 'show')}?encodedId=" + row;
    }

    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    /*allow delete when recordStatus is NEW  */
    function manageExecuteActions(row) {
        testRowList.push(row);
        if (${loanList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}) {
            return true;
        }
        return false;
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${loanList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
        var booleanRowStatus = testRow.loanRequest.requestStatus != "${g.message(code: 'EnumRequestStatus.SENT_BY_LIST')}";
        if (booleanListStatus && booleanRowStatus) {
            row.deleteCell(0);
            var x = row.insertCell(0);
            x.innerHTML = " ";
        }
    }


        function viewApproveModal() {
            var arrayLength = _dataTablesCheckBoxValues['loanListPersonTable'].length;
            if (arrayLength > 1) {
                gui.formValidatable.showErrorMessage("${message(code:'loanList.approveJustForOneRequest.label')}")
            } else if (arrayLength == 0) {
                gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
            } else {
                $('#hiddenApproveLink').click();
            }
        }

        function viewRejectModal() {
            var arrayLength = _dataTablesCheckBoxValues['loanListPersonTable'].length;
            if (arrayLength == 0) {
                gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
            } else {
                $('#hiddenRejectLink').click();
            }
        }
</script>
</body>
</html>


