<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:if test="${serviceList?.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE}">
        <g:set var="entities"
               value="${message(code: 'recallToService.entities', default: 'recallToService List')}"/>
        <g:set var="entity" value="${message(code: 'recallToService.entity', default: 'recallToService List')}"/>
    </g:if>
    <g:else>
        <g:set var="entities"
               value="${message(code: 'endOfService.entities', default: 'endOfService List')}"/>
        <g:set var="entity" value="${message(code: 'endOfService.entity', default: 'endOfService List')}"/>
    </g:else>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities])}"/>
    <title>${title}</title>

</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <g:if test="${serviceList?.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE}">
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'serviceList', action: 'listReturnToServiceList')}'"/>
        </g:if>
        <g:else>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'serviceList', action: 'listEndOfServiceList')}'"/>
        </g:else>

    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${serviceList?.code}" type="String"
                     label="${message(code: 'serviceList.code.label', default: 'code')}"/>
    <lay:showElement value="${serviceList?.name}" type="String"
                     label="${message(code: 'serviceList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${serviceList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'serviceList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${serviceList?.receivingParty}" type="enum"
                     label="${message(code: 'serviceList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/><br/>

<lay:collapseWidget id="endOfServiceCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="serviceListEmployeeForm" id="serviceListEmployeeForm">
            <el:hiddenField id="serviceList.id" name="serviceList.id" value="${serviceList.id}"/>
            <g:render template="/serviceListEmployee/search" model="[:]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['employmentServiceTableInServiceList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('serviceListEmployeeForm');_dataTables['employmentServiceTableInServiceList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:row/>
<el:dataTable id="employmentServiceTableInServiceList"
              searchFormName="serviceListEmployeeForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="serviceListEmployee"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="serviceListEmployee" rowCallbackFunction="manageRow"
              domainColumns="DOMAIN_COLUMNS">

    <el:dataTableAction
            controller="employmentServiceRequest"
            action="show"
            class="green icon-eye"
            actionParams="['requestEncodedId']"
            showFunction="manageShowAction"
            message="${message(code: 'default.show.label', args: [entity], default: 'show serviceRequest')}"/>

    <el:dataTableAction
            controller="serviceListEmployee"
            action="delete"
            showFunction="manageExecuteActions"
            class="red icon-cancel"
            type="confirm-delete"
            actionParams="encodedId"
            message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete serviceList')}"/>
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="id"
            controller="serviceList"
            action="noteList"
            class="black icon-info-4"
            type="modal-ajax"
            message="${message(code: 'serviceList.noteList.label')}"/>
</el:dataTable>
<br/><br/>


<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${serviceList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'serviceList', action: 'addRequestModal', id: serviceList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

%{--this code was committed to fix the bug: EPHR-2036 and if they ask about exact exceptional filter (customer) we will work on it--}%
%{--<g:if test="${serviceList.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE && serviceList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">--}%
%{--<el:modalLink--}%
%{--link="${createLink(controller: 'serviceList', action: 'addExceptionModal', id: serviceList?.encodedId)}"--}%
%{--preventCloseOutSide="true" class=" btn btn-sm btn-danger"--}%
%{--label="">--}%
%{--<i class="ace-icon icon-block-2"></i>${message(code: 'list.addExceptional.label')}--}%
%{--</el:modalLink>--}%
%{--</g:if>--}%

    <g:if test="${serviceList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'serviceList', action: 'sendListModal', id: serviceList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'serviceList', action: 'receiveListModal', id: serviceList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${serviceList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
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
                      link="${createLink(controller: 'serviceList', action: 'closeListModal', id: serviceList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>
    <btn:attachmentButton onClick="openAttachmentModal('${serviceList?.id}')"/>


    <report:staticViewList fileName="correspondenceTemplate" withDataTable="employmentServiceTableInServiceList"
                           searchFromName="serviceListEmployeeForm"
                           domain="serviceListEmployee" method="getReportData"
                           columns="DOMAIN_COLUMNS" format="pdf" title="${message(code: 'serviceList.entity')}"/>

</div>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>

    var testRowList = [];
    var counter = 0;

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
        if (${serviceList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}) {
            return true;
        }
        return false;
    }

    function manageShowAction(row) {
        if (!row.requestId || row.requestId == "") {
            return false;
        } else {
            return true;
        }
    }

    // view approvel modal only when a request is selected
    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['employmentServiceTableInServiceList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'serviceList', action: 'approveRequestModal', id: serviceList?.encodedId)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['employmentServiceTableInServiceList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'serviceList', action: 'rejectRequestModal', id: serviceList?.encodedId)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${serviceList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
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


