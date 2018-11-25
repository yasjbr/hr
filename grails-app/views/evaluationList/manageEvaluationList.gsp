<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'employeeEvaluation.entities', default: 'employeeEvaluation List')}"/>
    <g:set var="entity" value="${message(code: 'employeeEvaluation.entity', default: 'employeeEvaluation')}"/>
    <g:set var="title" value="${message(code: 'evaluationList.label', args: [entities])}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'evaluationList', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
    <lay:showElement value="${evaluationList?.code}" type="String"
                     label="${message(code: 'evaluationList.code.label', default: 'code')}"/>
    <lay:showElement value="${evaluationList?.name}" type="String"
                     label="${message(code: 'evaluationList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${evaluationList?.currentStatus?.correspondenceListStatus}" type="enum"
                     label="${message(code: 'evaluationList.currentStatus.correspondenceListStatus.label', default: 'correspondenceListStatus')}"/>
    <lay:showElement value="${evaluationList?.receivingParty}" type="enum"
                     label="${message(code: 'evaluationList.receivingParty.label', default: 'receivingParty')}"/>
</lay:showWidget>
<el:row/>
<br/>
<lay:collapseWidget id="employeeEvaluationCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="employeeEvaluationSearchForm"
                 id="employeeEvaluationSearchForm">
            <el:hiddenField name="evaluationList.id" value="${evaluationList.id}"/>
            <g:render template="/evaluationListEmployee/search"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['employeeEvaluationTableInEvaluationList'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('employeeEvaluationSearchForm');_dataTables['employeeEvaluationTableInEvaluationList'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>
    <el:dataTable id="employeeEvaluationTableInEvaluationList" searchFormName="employeeEvaluationSearchForm"
                  dataTableTitle="${message(code: 'default.list.label', args: [entities])}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="evaluationListEmployee"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="evaluationListEmployee"
                  domainColumns="DOMAIN_COLUMNS">
        <el:dataTableAction controller="employeeEvaluation" action="show" class="green icon-eye"
                            actionParams="requestEncodedId"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show employeeEvaluation')}"/>
        <el:dataTableAction controller="evaluationListEmployee" action="delete"
                            showFunction="manageExecuteActions" class="red icon-cancel" type="confirm-delete"
                            actionParams="encodedId"
                            message="${message(code: 'list.removeRecord.label', args: [entity], default: 'delete evaluationListEmployee record')}"/>
        %{--show modal with note details--}%
        <el:dataTableAction controller="evaluationList" preventCloseOutSide="true"
                            actionParams="id" action="noteList"
                            class="black icon-info-4" type="modal-ajax"
                            message="${message(code: 'evaluationList.noteList.label')}"/>

    </el:dataTable>

<br/><br/>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

    <g:if test="${evaluationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink
                link="${createLink(controller: 'evaluationList', action: 'addRequestModal', id: evaluationList?.encodedId)}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="">
            <i class="ace-icon icon-plus"></i>${message(code: 'list.addRequest.label')}
        </el:modalLink>
    </g:if>
    
    <g:if test="${evaluationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'evaluationList', action: 'sendListModal', id: evaluationList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-paper-plane"></i>${message(code: 'list.sendList.label')}
        </el:modalLink>
    </g:if>

    <g:if test="${evaluationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-approve"
                      link="#"
                      label="" style="display: none;" id="hiddenApproveLink">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </el:modalLink>
        <button class=" btn btn-sm btn-approve" onclick="viewApproveModal()">
            <i class="ace-icon icon-ok"></i>${message(code: 'list.approveRequest.label')}
        </button>
    </g:if>

    <g:if test="${evaluationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-danger"
                      link="#"
                      label="" style="display: none;" id="hiddenRejectLink">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </el:modalLink>
        <button class=" btn btn-sm btn-danger" onclick="viewRejectModal()">
            <i class="ace-icon icon-cancel"></i>${message(code: 'list.rejectRequest.label')}
        </button>
    </g:if>

    <g:if test="${evaluationList?.currentStatus?.correspondenceListStatus == ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.RECEIVED}">
        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                      link="${createLink(controller: 'evaluationList', action: 'closeListModal', id: evaluationList?.encodedId)}"
                      label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'list.closeList.label')}
        </el:modalLink>
    </g:if>

<g:if test="${evaluationList?.currentStatus?.correspondenceListStatus != ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED}">
    <el:linkGenerator class=" btn btn-sm btn-primary"
                  link="${createLink(controller: 'evaluationListEmployee', action: 'exportExcel', id: evaluationList?.id)}"
                  label="">
        <i class="ace-icon icon-export"></i>${message(code: 'employeeEvaluation.export.label')}
    </el:linkGenerator>
</g:if>

    <btn:attachmentButton onClick="openAttachmentModal('${evaluationList?.id}')"/>

    <report:staticViewList fileName="correspondenceTemplate"  withDataTable="employeeEvaluationTableInEvaluationList"
                           searchFromName="employeeEvaluationSearchForm"
                           domain="evaluationListEmployee" method="getReportData"
                           columns="LIST_DOMAIN_COLUMNS"  format="pdf" title="${message(code: 'evaluationList.entity')}"  />

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

    function showTheRecord(showLink) {
        window.location.href = showLink;

    }

    /*allow delete when list Status is CREATED  */
    function manageExecuteActions(row) {
        return (${evaluationList?.currentStatus?.correspondenceListStatus==ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus.CREATED});
    }

    // view approvel modal only when a request is selected
    function viewApproveModal() {
        var arrayLength = _dataTablesCheckBoxValues['employeeEvaluationTableInEvaluationList'].length;
        if(arrayLength == 0){
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'evaluationList', action: 'approveRequestModal', id: evaluationList?.encodedId)}";
            $('#hiddenApproveLink').attr("href", url);
            $('#hiddenApproveLink').click();
        }
    }

    // view reject modal only when a request is selected
    function viewRejectModal() {
        var arrayLength = _dataTablesCheckBoxValues['employeeEvaluationTableInEvaluationList'].length;
        if(arrayLength == 0){
            gui.formValidatable.showErrorMessage("${message(code:'list.request.notSelected.error')}");
        } else {
            var url = "${createLink(controller: 'evaluationList', action: 'rejectRequestModal', id: evaluationList?.encodedId)}";
            $('#hiddenRejectLink').attr("href", url);
            $('#hiddenRejectLink').click();
        }
    }
</script>
</body>
</html>


