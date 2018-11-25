<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'applicantInspectionResultList.entities', default: 'applicantInspectionResultRequest List')}"/>
    <g:set var="entity"
           value="${message(code: 'applicantInspectionResultList.entity', default: 'applicantInspectionResultRequest ')}"/>

    <g:set var="applicant"
           value="${message(code: 'applicant.entity', default: 'applicant ')}"/>


    <g:set var="title" value="${message(code: 'applicantInspectionResultList.label', args: [entities])}"/>
    <title>${title}</title>
</head>

<body>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'applicantInspectionResultList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${applicantInspectionResultList?.code}" type="String"
                     label="${message(code: 'applicantInspectionResultList.code.label', default: 'code')}"/>
    <lay:showElement value="${applicantInspectionResultList?.name}" type="String"
                     label="${message(code: 'applicantInspectionResultList.name.label', default: 'name')}"/>
    <lay:showElement value="${applicantInspectionResultList?.inspectionCategory?.descriptionInfo?.localName}"
                     type="string"
                     label="${message(code: 'applicantInspectionResultList.inspectionCategory.label', default: 'inspectionCategory')}"/>

</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${applicantInspectionResultList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'applicantInspectionResultList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${applicantInspectionResultList?.transientData?.organizationName}" type="string"
                     label="${message(code: 'applicantInspectionResultList.coreOrganizationId.label', default: 'coreOrganizationId')}"/>

    <lay:showElement value="${applicantInspectionResultList?.inspection?.descriptionInfo?.localName}"
                     type="string"
                     label="${message(code: 'applicantInspectionResultList.inspection.label', default: 'inspection')}"/>

</lay:showWidget>
<el:row/>
<br/>
<lay:collapseWidget id="applicantInspectionResultCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="applicantInspectionResultRequestSearchForm"
                 id="applicantInspectionResultRequestSearchForm">
            <el:hiddenField id="applicantInspectionResultList.id" name="applicantInspectionResultList.id"
                            value="${applicantInspectionResultList.id}"/>
            <g:render template="/applicantInspectionResultListEmployee/search" model="[:]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['applicantTableInApplicantInspectionResultList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('applicantInspectionResultRequestSearchForm');_dataTables['applicantTableInApplicantInspectionResultList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>

<el:dataTable id="applicantTableInApplicantInspectionResultList"
              searchFormName="applicantInspectionResultRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="applicantInspectionResultListEmployee"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="applicantInspectionResultListEmployee" rowCallbackFunction="manageRow"
              domainColumns="DOMAIN_COLUMNS">

    <el:dataTableAction controller="applicant" action="show" class="green icon-eye"
                        actionParams="applicant.encodedId"
                        message="${message(code: 'default.show.label', args: [applicant], default: 'show applicant')}"/>


    <el:dataTableAction controller="applicantInspectionResultListEmployee" action="delete"
                        showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                        actionParams="encodedId"
                        message="${message(code: 'list.removeRecord.label', args: [applicant], default: 'delete applicant record')}"/>


%{--show modal with note details--}%
    <el:dataTableAction controller="applicantInspectionResultList" preventCloseOutSide="true"
                        actionParams="id" action="noteList"
                        class="black icon-info-4" type="modal-ajax"
                        message="${message(code: 'applicantInspectionResultList.noteList.label')}"/>

</el:dataTable>

<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${applicantInspectionResultList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'applicantInspectionResultList', action: 'addRequestModal', params: [encodedId: applicantInspectionResultList?.encodedId])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${applicantInspectionResultList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'applicantInspectionResultList', action: 'sendListModal', params: [encodedId: applicantInspectionResultList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${showReceiveList}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'applicantInspectionResultList', action: 'receiveListModal', params: [encodedId: applicantInspectionResultList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-mail"></i>${message(code: 'list.receiveList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${applicantInspectionResultList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-approve"
                      link="${createLink(controller: 'applicantInspectionResultList', action: 'approveRequestModal', params: [encodedId: applicantInspectionResultList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.positive.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${applicantInspectionResultList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-danger"
                      link="${createLink(controller: 'applicantInspectionResultList', action: 'rejectRequestModal', params: [encodedId: applicantInspectionResultList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.negative.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${applicantInspectionResultList?.currentStatus?.correspondenceListStatus in [ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED, ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED]}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'applicantInspectionResultList', action: 'closeListModal', params: [encodedId: applicantInspectionResultList?.encodedId])}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>

    <btn:attachmentButton onClick="openAttachmentModal('${applicantInspectionResultList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate"
                           withDataTable="applicantTableInApplicantInspectionResultList"
                           searchFromName="applicantInspectionResultRequestSearchForm"
                           domain="applicantInspectionResultListEmployee" method="getReportData"
                           columns="LIST_DOMAIN_COLUMNS" format="pdf"
                           title="${message(code: 'applicantInspectionResultList.entity')}"/>

</div>

<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>
    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }

    var testRowList = [];
    var counter = 0;

    function showTheRecord(showLink) {
        window.location.href = showLink;

    }

    /*allow delete when list Status is CREATED  */
    function manageExecuteActions(row) {
        testRowList.push(row);
        return (${applicantInspectionResultList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED});
    }

    function manageRow(row) {
        var testRow = testRowList[counter++];
        var booleanListStatus = "${applicantInspectionResultList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.PARTIALLY_CLOSED}";
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


