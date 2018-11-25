<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'promotionListEmployee.entities', default: 'promotionListEmployee List')}"/>
    <g:set var="entity" value="${message(code: 'promotionListEmployee.entity', default: 'promotionListEmployee')}"/>
    <g:set var="title" value="${message(code: 'promotionList.label', args: [entities])}"/>
    <title>${title}</title>

</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'promotionList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${promotionList?.code}" type="String"
                     label="${message(code: 'promotionList.code.label', default: 'code')}"/>
    <lay:showElement value="${promotionList?.name}" type="String"
                     label="${message(code: 'promotionList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${promotionList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'promotionList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${promotionList?.receivingParty}" type="enum"
                     label="${message(code: 'promotionList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>

<lay:collapseWidget id="promotionCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="promotionListEmployeeSearchForm"
                 id="promotionListEmployeeSearchForm">
            <el:hiddenField id="promotionList.id" name="promotionList.id" value="${promotionList.id}"/>
            <g:render template="/promotionListEmployee/search"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['promotionListEmployeeTableInPromotionList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('promotionListEmployeeSearchForm');_dataTables['promotionListEmployeeTableInPromotionList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>

<el:dataTable id="promotionListEmployeeTableInPromotionList" searchFormName="promotionListEmployeeSearchForm"
              dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="promotionListEmployee"
              spaceBefore="true"
              hasRow="true" rowCallbackFunction="manageRow"
              action="filter"
              serviceName="promotionListEmployee">
    <el:dataTableAction functionName="showTheRecord" showFunction="manageShowAction" type="function"
                        class="green icon-eye"
                        actionParams="['showLink']"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show promotionListEmployee')}"/>

%{--show modal with note details--}%
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="id" controller="promotionList" action="noteList"
            class="black icon-info-4" type="modal-ajax"
            message="${message(code: 'promotionList.noteList.label')}"/>

    <el:dataTableAction controller="promotionListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete promotion')}"/>

</el:dataTable>

<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${promotionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'promotionList', action: 'addRequestModal', id: promotionList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'promotionList.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${promotionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'promotionList', action: 'addEligibleEmployeeModal', id: promotionList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-grey"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'promotionList.addEligibleEmployee.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${promotionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'promotionList', action: 'addExceptionEmployeeModal', id: promotionList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-danger"
                label="">
            <i class="ace-icon icon-block-2">${message(code: 'promotionList.addExceptionEmployee.label')}</i>
        </el:modalLink>
    </g:if>

    <g:if test="${promotionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'promotionList', action: 'sendListModal', id: promotionList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'promotionList', action: 'receiveListModal', id: promotionList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>


    <g:if test="${promotionList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
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
                      link="${createLink(controller: 'promotionList', action: 'closeListModal', id: promotionList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>
    <btn:attachmentButton onClick="openAttachmentModal('${promotionList?.id}')"/>


    <report:staticViewList fileName="correspondenceTemplate" withDataTable="promotionListEmployeeTableInPromotionList"
                           searchFromName="promotionListEmployeeSearchForm"
                           domain="promotionListEmployee" method="getReportData"
                           columns="DOMAIN_COLUMNS" format="pdf" title="${message(code: 'promotionList.entity')}"/>

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

    /*allow delete when recordStatus is NEW  */
    function manageExecuteActions(row) {
        testRowList.push(row);
        if (${promotionList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}) {
            return true;
        }
        return false;
    }

    /*the show is needed when the record comes from request */
    function manageShowAction(row) {
        if (row.isRequest == true || row.isRequest == "true") {
            return true;
        }
        return false;
    }


    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['promotionListEmployeeTableInPromotionList'].length;
        var selectedRow = _dataTablesCheckBoxValues['promotionListEmployeeTableInPromotionList'];
        console.log("selectedRow: " + selectedRow);
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}")
        } else if (arrayLength > 1) {
            gui.formValidatable.showErrorMessage("${message(code:'promotionList.approveJustForOneRequest.label')}")
        } else {
            var url = "${createLink(controller: 'promotionList',action: 'approveRequestModal')}" + "?encodedId=${promotionList?.encodedId}&selectedRow=" + selectedRow;
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['promotionListEmployeeTableInPromotionList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}")
        } else {
            var url = "${createLink(controller: 'promotionList', action: 'rejectRequestModal', id: promotionList?.encodedId)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${promotionList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
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


