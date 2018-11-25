<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'maritalStatusRequest.entities', default: 'maritalStatusList List')}"/>
    <g:set var="entity" value="${message(code: 'maritalStatusList.entity', default: 'maritalStatusList')}"/>
    <g:set var="title" value="${message(code: 'maritalStatusList.label', args: [entities])}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'maritalStatusList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${maritalStatusList?.code}" type="String"
                     label="${message(code: 'maritalStatusList.code.label', default: 'code')}"/>
    <lay:showElement value="${maritalStatusList?.name}" type="String"
                     label="${message(code: 'maritalStatusList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${maritalStatusList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'maritalStatusList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${maritalStatusList?.receivingParty}" type="enum"
                     label="${message(code: 'maritalStatusList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>
<lay:collapseWidget id="childCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="maritalStatusRequestSearchForm"
                 id="maritalStatusRequestSearchForm">
            <el:hiddenField id="maritalStatusList.id" name="maritalStatusList.id" value="${maritalStatusList.id}"/>
            <g:render template="/maritalStatusListEmployee/search"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['maritalStatusRequestTableInMaritalStatusList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('maritalStatusRequestSearchForm');_dataTables['maritalStatusRequestTableInMaritalStatusList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>

<el:dataTable id="maritalStatusRequestTableInMaritalStatusList" searchFormName="maritalStatusRequestSearchForm"
              dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="maritalStatusListEmployee"
              spaceBefore="true"
              hasRow="true" rowCallbackFunction="manageRow"
              action="filterRequest"
              serviceName="maritalStatusListEmployee"
              domainColumns="LIST_DOMAIN_COLUMNS">
    <el:dataTableAction controller="maritalStatusRequest" action="show" class="green icon-eye"
                        actionParams="requestEncodedId"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show maritalStatusRequest')}"/>
    <el:dataTableAction controller="maritalStatusListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete maritalStatusListEmployee record')}"/>
%{--show modal with note details--}%
    <el:dataTableAction controller="maritalStatusList" preventCloseOutSide="true"
                        actionParams="id" action="noteList"
                        class="black icon-info-4" type="modal-ajax"
                        message="${message(code: 'maritalStatusList.noteList.label')}"/>

</el:dataTable>

<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${maritalStatusList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'maritalStatusList', action: 'addRequestModal', id: maritalStatusList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${maritalStatusList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'maritalStatusList', action: 'sendListModal', id: maritalStatusList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'maritalStatusList', action: 'receiveListModal', id: maritalStatusList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${maritalStatusList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
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
                      link="${createLink(controller: 'maritalStatusList', action: 'closeListModal', id: maritalStatusList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>

    <btn:attachmentButton onClick="openAttachmentModal('${maritalStatusList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate"
                           withDataTable="maritalStatusRequestTableInMaritalStatusList"
                           searchFromName="maritalStatusRequestSearchForm"
                           domain="maritalStatusListEmployee" method="getReportData"
                           columns="LIST_DOMAIN_COLUMNS" format="pdf"
                           title="${message(code: 'maritalStatusList.entity')}"/>

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
        return (${maritalStatusList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED});
    }

    // view approvel modal only when a request is selected
    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['maritalStatusRequestTableInMaritalStatusList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'maritalStatusList', action: 'approveRequestModal', id: maritalStatusList?.encodedId)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['maritalStatusRequestTableInMaritalStatusList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'maritalStatusList', action: 'rejectRequestModal', id: maritalStatusList?.encodedId)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }
    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${maritalStatusList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
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


