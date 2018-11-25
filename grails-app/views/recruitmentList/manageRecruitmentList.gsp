<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'applicant.entities', default: 'applicant List')}"/>
    <g:set var="trainingEntities"
           value="${message(code: 'traineeList.trainees.label', default: 'applicant List')}"/>
    <g:set var="entity" value="${message(code: 'applicant.entity', default: 'applicant')}"/>
    <g:set var="title" value="${message(code: 'recruitmentList.label', args: [entities])}"/>
    <title>${title}</title>

</head>

<body>
<msg:page/>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${recruitmentList?.code}" type="String"
                     label="${message(code: 'recruitmentList.code.label', default: 'code')}"/>
    <lay:showElement value="${recruitmentList?.name}" type="String"
                     label="${message(code: 'recruitmentList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${recruitmentList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'recruitmentList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${recruitmentList?.receivingParty}" type="enum"
                     label="${message(code: 'recruitmentList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>
%{--search for recruitment list by applicant--}%
<lay:collapseWidget id="applicantCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'recruitmentList', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="acceptRecruitmentListEmployeeForm" id="acceptRecruitmentListEmployeeForm">
            <el:hiddenField name="recruitmentList.id" value="${recruitmentList?.id}"/>
            <g:render template="/applicant/searchForList" model="[:]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['applicantTableInRecruitmentList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('acceptRecruitmentListEmployeeForm');_dataTables['applicantTableInRecruitmentList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>


<br/>
<el:dataTable id="applicantTableInRecruitmentList"
              searchFormName="acceptRecruitmentListEmployeeForm"
              dataTableTitle="${message(code: 'recruitmentList.recruitmentRequests.label', default: 'traineeRequests')}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="recruitmentList"
              spaceBefore="true"
              hasRow="true"
              action="filterApplicant"
              serviceName="applicant"
              domainColumns="DOMAIN_TAB_COLUMNS" rowCallbackFunction="manageRow">

    <el:dataTableAction controller="applicant" action="show" class="green icon-eye"
                        actionParams="['recruitmentListId', 'encodedId']"
                        message="${message(code: 'default.show.label', args: [entity], default: 'show applicant')}"/>
    <el:dataTableAction controller="recruitmentListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete recruitmentList')}"/>
    <el:dataTableAction
            preventCloseOutSide="true"
            actionParams="id" controller="recruitmentList" action="noteList"
            class="black icon-info-4" type="modal-ajax"
            message="${message(code: 'recruitmentList.noteList.label')}"/>

    <el:dataTableAction
            actionParams="personId" controller="employee" action="createNewEmployee"
            class="blue icon-user-plus" showFunction="manageCreateProfile"
            accessUrl="${createLink(controller: "employee", action: "createNewEmployee")}"
            message="${message(code: 'recruitmentList.createNewEmployee.label')}"/>
</el:dataTable>
<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${recruitmentList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'recruitmentList', action: 'addEligibleApplicantsModal', id: recruitmentList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'recruitmentList.addApplicant.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${recruitmentList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'recruitmentList', action: 'addExceptionApplicantsModal', id: recruitmentList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-danger"
                label="">
            <i class="ace-icon icon-block-2">${message(code: 'recruitmentList.addApplicantToListAsSpecialCaseForm.label')}</i>
        </el:modalLink>
    </g:if>

    <g:if test="${recruitmentList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'recruitmentList', action: 'sendListModal', id: recruitmentList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'recruitmentList', action: 'receiveListModal', id: recruitmentList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>


    <g:if test="${recruitmentList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
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
                      link="${createLink(controller: 'recruitmentList', action: 'closeListModal', id: recruitmentList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>

    <btn:attachmentButton onClick="openAttachmentModal('${recruitmentList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate" withDataTable="applicantTableInRecruitmentList"
                           searchFromName="acceptRecruitmentListEmployeeForm" isSystemGenerate="false"
                           domain="applicant" method="getRecruitmentReportData"
                           columns="DOMAIN_TAB_CUSTOM_COLUMNS" format="pdf"
                           title="${message(code: 'recruitmentList.entity')}"/>

</div>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>
    var testRowList = [];
    var counter = 0;

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${recruitmentList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
        var booleanRowStatus = testRow.applicantCurrentStatus.applicantStatus != "${g.message(code: 'EnumApplicantStatus.ADD_TO_LIST')}";
        if (booleanListStatus && booleanRowStatus) {
            row.deleteCell(0);
            var x = row.insertCell(0);
            x.innerHTML = " ";
        }
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
        if (${recruitmentList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}) {
            return true;
        }
        return false;
    }


    /*allow create employee when recordStatus is APPROVED*/
    function manageCreateProfile(row) {
        return row.applicantCurrentStatus.applicantStatus == "${g.message(code:'EnumApplicantStatus.EMPLOYED')}"
    }
    /*to allow add note to request until close the list*/
    function showNoteInList() {
        if (${recruitmentList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CLOSED}) {
            return false
        }
        return true
    }

    // view approval modal only when a request is selected
    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['applicantTableInRecruitmentList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'recruitmentList', action: 'approveRequestModal', id: recruitmentList?.encodedId)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['applicantTableInRecruitmentList'].length;
        if (arrayLength == 0) {
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'recruitmentList', action: 'rejectRequestModal', id: recruitmentList?.encodedId)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }
</script>
</body>
</html>


