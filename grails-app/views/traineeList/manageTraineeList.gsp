<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'applicant.entities', default: 'applicant List')}"/>
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant')}"/>
    <g:set var="title" value="${message(code: 'traineeList.label', args: [entities])}"/>
    <title>${title}</title>

</head>

<body>
<msg:page/>

<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${traineeList?.code}" type="String"
                     label="${message(code: 'traineeList.code.label', default: 'code')}"/>
    <lay:showElement value="${traineeList?.name}" type="String"
                     label="${message(code: 'traineeList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${traineeList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'traineeList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${traineeList?.receivingParty}" type="enum"
                     label="${message(code: 'traineeList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>

%{--search for trainee list by applicant--}%
<lay:collapseWidget id="applicantCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'traineeList', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="acceptTraineeListEmployeeForm" id="acceptTraineeListEmployeeForm">
            <el:hiddenField name="traineeList.id" value="${traineeList?.id}"/>
            <g:render template="/traineeListEmployee/search" />
            <el:formButton functionName="search"
                           onClick="_dataTables['applicantTableInTraineeList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('acceptTraineeListEmployeeForm');_dataTables['applicantTableInTraineeList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>
<br/>
    <el:dataTable id="applicantTableInTraineeList"
              searchFormName="acceptTraineeListEmployeeForm"
              dataTableTitle="${message(code: 'traineeList.traineeRequests.label', default: 'traineeRequests')}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="traineeListEmployee"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="traineeListEmployee"
              domainColumns="DOMAIN_COLUMNS">

    <el:dataTableAction controller="applicant" action="show" class="green icon-eye"
                        actionParams="['traineeListId', 'encodedId']"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show applicant')}"/>
    <el:dataTableAction controller="traineeListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete traineeList')}"/>
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="id" controller="traineeList" action="noteList"
            class="black icon-info-4" type="modal-ajax"
            message="${message(code: 'traineeList.noteList.label')}"/>

%{--show modal with inspection details--}%
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="id" controller="traineeList" action="inspectionList"
            class="black icon-info-circled-alt" type="modal-ajax"
            message="${message(code: 'traineeList.inspectionCategory.label')}"/>



</el:dataTable>
<br/><br/>


<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${traineeList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'traineeList', action: 'addEligibleApplicantsModal', id: traineeList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'traineeList.addApplicant.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${traineeList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'traineeList', action: 'addExceptionApplicantsModal', id: traineeList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-danger"
                label="">
            <i class="ace-icon icon-block-2">${message(code: 'traineeList.addApplicantToListAsSpecialCaseForm.label')}</i>
        </el:modalLink>
    </g:if>

    <g:if test="${traineeList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'traineeList', action: 'sendListModal', id: traineeList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'traineeList', action: 'receiveListModal', id: traineeList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>


    <g:if test="${traineeList?.currentStatus?.correspondenceListStatus in[ ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
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
                      link="${createLink(controller: 'traineeList', action: 'closeListModal', id: traineeList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>

    </g:if>


    <btn:attachmentButton onClick="openAttachmentModal('${traineeList?.id}')" />

    <report:staticViewList fileName="correspondenceTemplate"  withDataTable="applicantTableInTraineeList"
                           searchFromName="acceptTraineeListEmployeeForm" isSystemGenerate="false"
                           domain="applicant" method="getReportData"
                           columns="DOMAIN_TRAINEE_LIST_CUSTOM_COLUMNS"  format="pdf" title="${message(code: 'traineeList.entity')}"  />

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
        $("#attachmentListModal").attr('referenceObject',"${referenceObject}" );
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList',  "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }
    
    /*allow delete when recordStatus is NEW  */
    function manageExecuteActions(row) {
        testRowList.push(row);
        if (${traineeList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}) {
            return true;
        }
        return false;
    }
    /*to allow add note to request until close the list*/
    function showNoteInList() {
        if (${traineeList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CLOSED}) {
            return false
        }
        return true
    }

    // view approvel modal only when a request is selected
    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['applicantTableInTraineeList'].length;
        if(arrayLength == 0){
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'traineeList', action: 'approveRequestModal', id: traineeList?.encodedId)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['applicantTableInTraineeList'].length;
        if(arrayLength == 0){
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'traineeList', action: 'rejectRequestModal', id: traineeList?.encodedId)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }


    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${traineeList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
        var booleanRowStatus = testRow.applicantCurrentStatus.applicantStatus != "${g.message(code: 'EnumApplicantStatus.ADD_TO_LIST')}";
        if (booleanListStatus && booleanRowStatus) {
            row.deleteCell(0);
            var x = row.insertCell(0);
            x.innerHTML = " ";
        }
    }
</script>
</body>
</html>


