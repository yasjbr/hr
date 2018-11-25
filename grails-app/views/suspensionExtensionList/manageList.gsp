<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'suspensionExtensionRequest.entities', default: 'suspensionExtensionRequest List')}"/>
    <g:set var="entity"
           value="${message(code: 'suspensionExtensionRequest.entity', default: 'suspensionExtensionRequest')}"/>
    <g:set var="title" value="${message(code: 'suspensionExtensionList.label', args: [entities])}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'suspensionExtensionList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${suspensionExtensionList?.code}" type="String"
                     label="${message(code: 'suspensionExtensionList.code.label', default: 'code')}"/>
    <lay:showElement value="${suspensionExtensionList?.name}" type="String"
                     label="${message(code: 'suspensionExtensionList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${suspensionExtensionList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'suspensionExtensionList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${suspensionExtensionList?.receivingParty}" type="enum"
                     label="${message(code: 'suspensionExtensionList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>
<lay:collapseWidget id="suspensionExtensionCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="suspensionExtensionRequestSearchForm"
                 id="suspensionExtensionRequestSearchForm">
            <el:hiddenField id="suspensionExtensionList.id" name="suspensionExtensionList.id"
                            value="${suspensionExtensionList.id}"/>
            <g:render template="/suspensionExtensionListEmployee/search" model="[manageSearch: true]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['suspensionExtensionRequestTableInSuspensionExtensionList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('suspensionExtensionRequestSearchForm');_dataTables['suspensionExtensionRequestTableInSuspensionExtensionList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>

<el:dataTable id="suspensionExtensionRequestTableInSuspensionExtensionList"
              searchFormName="suspensionExtensionRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="suspensionExtensionListEmployee"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="suspensionExtensionListEmployee" rowCallbackFunction="manageRow"
              domainColumns="DOMAIN_COLUMNS">

    <el:dataTableAction controller="suspensionRequest" action="extensionRequestShow" class="green icon-eye"
                        actionParams="suspensionExtensionRequest.encodedId" type="modal-ajax"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show suspensionExtensionRequest')}"/>


    <el:dataTableAction controller="suspensionExtensionListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete suspensionExtensionListEmployee record')}"/>


%{--show modal with note details--}%
    <el:dataTableAction controller="suspensionExtensionList" preventCloseOutSide="true"
                        actionParams="id" action="noteList"
                        class="black icon-info-4" type="modal-ajax"
                        message="${message(code: 'suspensionExtensionList.noteList.label')}"/>

</el:dataTable>

<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${suspensionExtensionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'suspensionExtensionList', action: 'addRequestModal', params: [encodedId: suspensionExtensionList?.encodedId])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${suspensionExtensionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'suspensionExtensionList', action: 'sendListModal', params: [encodedId: suspensionExtensionList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${suspensionExtensionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.SUBMITTED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'suspensionExtensionList', action: 'receiveListModal', params: [encodedId: suspensionExtensionList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${suspensionExtensionList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
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
                      link="${createLink(controller: 'suspensionExtensionList', action: 'closeListModal', params: [encodedId: suspensionExtensionList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>

    <btn:attachmentButton onClick="openAttachmentModal('${suspensionExtensionList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate"
                           withDataTable="suspensionExtensionRequestTableInSuspensionExtensionList"
                           searchFromName="suspensionExtensionRequestSearchForm"
                           domain="suspensionExtensionListEmployee" method="getReportData"
                           columns="LIST_DOMAIN_COLUMNS" format="pdf"
                           title="${message(code: 'suspensionExtensionList.entity')}"/>

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

    function showTheRecord(showLink) {
        window.location.href = showLink;

    }

    /*allow delete when list Status is CREATED  */
    function manageExecuteActions(row) {
        testRowList.push(row);
        return (${suspensionExtensionList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED});
    }

    // view approvel modal only when a request is selected
    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['suspensionExtensionRequestTableInSuspensionExtensionList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'suspensionExtensionList', action: 'approveRequestModal', params:[encodedId: suspensionExtensionList?.encodedId])}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['suspensionExtensionRequestTableInSuspensionExtensionList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'suspensionExtensionList', action: 'rejectRequestModal', params:[encodedId: suspensionExtensionList?.encodedId])}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${suspensionExtensionList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
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


