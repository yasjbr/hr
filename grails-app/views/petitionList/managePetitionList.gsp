<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'petitionRequest.entities', default: 'petitionRequest List')}"/>
    <g:set var="entity" value="${message(code: 'petitionRequest.entity', default: 'petitionRequest')}"/>
    <g:set var="title" value="${message(code: 'petitionList.label', args: [entities])}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'petitionList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${petitionList?.code}" type="String"
                     label="${message(code: 'petitionList.code.label', default: 'code')}"/>
    <lay:showElement value="${petitionList?.name}" type="String"
                     label="${message(code: 'petitionList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${petitionList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'petitionList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${petitionList?.receivingParty}" type="enum"
                     label="${message(code: 'petitionList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>
<lay:collapseWidget id="petitionCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="petitionRequestSearchForm" id="petitionRequestSearchForm">
            <el:hiddenField id="petitionList.id" name="petitionList.id" value="${petitionList.id}"/>
            <g:render template="/petitionListEmployee/search"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['petitionRequestTableInPetitionList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('petitionRequestSearchForm');_dataTables['petitionRequestTableInPetitionList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>

<el:dataTable id="petitionRequestTableInPetitionList" searchFormName="petitionRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="petitionListEmployee"
              spaceBefore="true"
              hasRow="true" rowCallbackFunction="manageRow"
              action="filter"
              serviceName="petitionListEmployee">
    <el:dataTableAction controller="petitionRequest" action="show" class="green icon-eye"
                        actionParams="requestEncodedId"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show petitionRequest')}"/>
    <el:dataTableAction controller="petitionListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete petitionListEmployee record')}"/>
%{--show modal with note details--}%
    <el:dataTableAction controller="petitionList" preventCloseOutSide="true"
                        actionParams="id" action="noteList"
                        class="black icon-info-4" type="modal-ajax"
                        message="${message(code: 'petitionList.noteList.label')}"/>

</el:dataTable>

<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${petitionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'petitionList', action: 'addRequestModal', id: petitionList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${petitionList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'petitionList', action: 'sendListModal', id: petitionList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'petitionList', action: 'receiveListModal', id: petitionList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${petitionList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-approve"
                      link="${createLink(controller: 'petitionList', action: 'approveRequestModal', id: petitionList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </el:modalLink>
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-danger"
                      link="${createLink(controller: 'petitionList', action: 'rejectRequestModal', id: petitionList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </el:modalLink>
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'petitionList', action: 'closeListModal', id: petitionList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>

    <btn:attachmentButton onClick="openAttachmentModal('${petitionList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate" withDataTable="petitionRequestTableInPetitionList"
                           searchFromName="petitionRequestSearchForm"
                           domain="petitionListEmployee" method="getReportData"
                           columns="LIST_DOMAIN_COLUMNS" format="pdf" title="${message(code: 'petitionList.entity')}"/>

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
        return (${petitionList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED});
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${petitionList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
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


