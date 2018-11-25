<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType; ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus; ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus; ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection; ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'aocListRecord.entities', default: 'allowanceRequest List')}"/>
    <g:set var="entity" value="${requestEntityName}"/>
    <g:set var="title" value="${message(code: 'allowanceList.manage.label')}"/>
    <title>${title}</title>
    <g:render template="script"/>
</head>

<body>
<msg:page/>
<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: listController, action: listAction,
                params: [correspondenceType: aocCorrespondenceList.correspondenceType?.toString()])}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>
<br/>
<lay:showWidget size="6">
%{--<lay:showElement value="${aocCorrespondenceList?.hrCorrespondenceList?.code}" type="String"--}%
%{--label="${message(code: 'allowanceList.code.label', default: 'code')}"/>--}%
    <lay:showElement value="${aocCorrespondenceList?.correspondenceName}" type="String"
                     label="${message(code: 'aocCorrespondenceList.name.label', default: 'name')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${aocCorrespondenceList?.correspondenceDirection}" type="enum"
                     label="${message(code: 'aocCorrespondenceList.correspondenceDirection.label', default: 'Direction')}"/>
    <lay:showElement value="${aocCorrespondenceList?.currentStatus}" type="enum"
                     label="${message(code: 'aocCorrespondenceList.currentStatus.label', default: 'currentStatus')}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${aocCorrespondenceList?.receivingParty?.name}" type="string"
                     label="${message(code: 'aocCorrespondenceList.TO.name.label', default: 'receivingParty')}"/>
    <lay:showElement value="${aocCorrespondenceList?.sendingParty?.name}" type="string"
                     label="${message(code: 'aocCorrespondenceList.FROM.name.label', default: 'sendingParty')}"/>
</lay:showWidget>

<lay:showWidget size="6">
    <g:if test="${aocCorrespondenceList?.incomingSerial}">
        <lay:showElement value="${aocCorrespondenceList?.incomingSerial}" type="String"
                         label="${message(code: 'aocCorrespondenceList.incomingSerial.label', default: 'incomingSerial')}"/>
    </g:if>
    <g:if test="${aocCorrespondenceList?.outgoingSerial}">
        <lay:showElement value="${aocCorrespondenceList?.outgoingSerial}" type="String"
                         label="${message(code: 'aocCorrespondenceList.outgoingSerial.label', default: 'outgoingSerial')}"/>
    </g:if>
    <lay:showElement value="${aocCorrespondenceList?.archivingDate}" type="ZonedDate"
                     label="${message(code: 'aocCorrespondenceList.archivingDate.label', args: [message(code: 'EnumCorrespondenceDirection.' + aocCorrespondenceList.correspondenceDirection)])}"/>
</lay:showWidget>
<lay:showWidget size="6">
    <lay:showElement value="${aocCorrespondenceList?.deliveryDate}" type="ZonedDate"
                     label="${message(code: 'aocCorrespondenceList.deliveryDate.label', default: 'deliveryDate')}"/>
    <g:if test="${aocCorrespondenceList?.originalSerialNumber}">
        <lay:showElement value="${aocCorrespondenceList?.originalSerialNumber}" type="String"
                         label="${message(code: 'aocCorrespondenceList.originalSerialNumber.label', default: 'originalSerialNumber')}"/>
    </g:if>
</lay:showWidget>

<lay:showWidget size="6">
    <lay:showElement value="${aocCorrespondenceList?.province?.descriptionInfo?.localName}" type="String"
                     label="${message(code: 'aocCorrespondenceList.province.label', default: 'province')}"/>

    <lay:showElement value="${aocCorrespondenceList?.provinceLocation?.transientData?.locationName}" type="String"
                     label="${message(code: 'aocCorrespondenceList.provinceLocation.label', default: 'provinceLocation')}"/>

</lay:showWidget>
<el:row/>
<br/>

<!--TODO .. delete here removes the aocListRecord, it should only remove the joinedCorrespondenceListRecord related to this correspondence-->

<form id="listRecordSearchForm">
    <el:hiddenField id="aocCorrespondenceListId" name="aocCorrespondenceList.id" value="${aocCorrespondenceList?.id}"/>
    <el:hiddenField id="correspondenceType" name="correspondenceType"
                    value="${aocCorrespondenceList?.correspondenceType}"/>

    <el:dataTable id="listRecordTableInAocList" searchFormName="listRecordSearchForm"
                  dataTableTitle="${title}" hasCheckbox="true" widthClass="col-sm-12" controller="aocListRecord"
                  spaceBefore="true" hasRow="true" action="filter" messagePrefix="${serviceName}"
                  serviceName="${serviceName}" domainColumns="domainColumns">

        <el:dataTableAction controller="aocListRecord"
                            accessUrl="${createLink(controller: 'aocListRecord', action: 'delete')}"
                            showFunction="manageExecuteActions" class="red icon-cancel" type="function"
                            actionParams="encodedId"
                            functionName="removeAocRecord"
                            message="${message(code: 'list.removeRecord.label', args: [entity], default: 'remove record')}"/>

    %{--show modal with note details--}%
        <el:dataTableAction preventCloseOutSide="true" showFunction="showNoteInList"
                            actionParams="id" controller="aocListRecordNote" action="list"
                            class="black icon-info-4 showNoteList" type="modal-ajax"
                            message="${message(code: 'dispatchList.noteList.label')}"/>

    %{--<el:dataTableAction preventCloseOutSide="true" showFunction="showChangeStatus"--}%
    %{--actionParams="id" controller="aocListRecordNote" action="createModal"--}%
    %{--class="black icon-note-1" type="modal-ajax"--}%
    %{--message="${message(code: 'dispatchListEmployeeNote.label')}"/>--}%



        <g:if test="${aocCorrespondenceList.correspondenceType != ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType.VIOLATION_LIST && aocCorrespondenceList.correspondenceType != EnumCorrespondenceType.EVALUATION_LIST}">
            <el:dataTableAction preventCloseOutSide="true" showFunction="showChangeStatus"
                                actionParams="encodedId" controller="aocListRecord" action="changeRecordStatusModal"
                                class="black icon-check showChangeStatus" type="modal-ajax"
                                message="${message(code: 'aocListRecord.changeStatus.label')}"/>

        </g:if>
    </el:dataTable>
</form>
<br/><br/>

%{--Called by workflow--}%
<g:if test="${workflowPathHeader}">
    <el:validatableForm title="${message(code: 'request.info.label')}" name="workflowForm"
                        callBackFunction="callBackWorkflowFunction"
                        controller="workflowPathDetails" action="update">

        <lay:widget size="12" transparent="true" color="blue" icon="icon-ok-circled"
                    title="${message(code: 'workflow.aocCorrespondenceList.requiredApprovalFromFirm.label')}">

            <lay:widgetBody>
                <el:hiddenField name="workflowPathHeader.id" value="${workflowPathHeader?.id}"/>
                <g:each in="${workflowPathHeader?.workflowPathDetails?.sort { it.sequence }}"
                        var="${workflowPathDetails}" status="index">

                    <el:row>
                    %{--show the  approved on request--}%
                        <g:if test="${workflowPathDetails?.workflowStatus in [EnumWorkflowStatus.APPROVED, EnumWorkflowStatus.FINAL_APPOVED,
                                                                              EnumWorkflowStatus.RECOMMEND_TO_APPROVE, EnumWorkflowStatus.RECOMMEND_TO_REJECT,
                                                                              EnumWorkflowStatus.REJECT]}">
                            <el:formGroup>
                                <el:labelField
                                        value="${message(code: 'EnumWorkflowStatus.' + workflowPathDetails?.workflowStatus, default: "${workflowPathDetails?.workflowStatus}")}"
                                        size="6"
                                        label="${workflowPathDetails?.transientData?.toJobTitleName}"/>
                            </el:formGroup>
                        </g:if>


                    %{--action form for current user --}%
                        <g:if test="${ps.police.common.utils.v1.PCPSessionUtils.getValue("jobTitleId") == workflowPathDetails?.toJobTitle &&
                                workflowPathDetails?.workflowStatus in [EnumWorkflowStatus.WAIT_FOR_APPROVAL, EnumWorkflowStatus.NOT_SEEN]}">
                            <el:formGroup>
                                <el:hiddenField name="id" value="${workflowPathDetails?.id}"/>
                                <el:hiddenField name="jobTitle" value="${workflowPathDetails?.toJobTitle}"/>
                                <el:hiddenField name="processedBy" value="${employeeId}"/>
                                <g:render template="/workflowPathDetails/manage"
                                          model="[workflowPathDetails: workflowPathDetails]"/>
                            </el:formGroup>
                        </g:if>

                    </el:row>
                </g:each>
            </lay:widgetBody>
        </lay:widget>

        <el:formButton isSubmit="true" functionName="save"/>
    </el:validatableForm>

</g:if>

<div class="clearfix form-actions text-center" style="background:gainsboro;">

%{--<g:if test="${actionMatrix.editList}">--}%
%{--<btn:editButton--}%
%{--onClick="window.location.href='${createLink(controller: 'aocCorrespondenceList', action: 'edit', params:[encodedId: aocCorrespondenceList?.encodedId])}'"/>--}%
%{--</g:if>--}%

    <g:if test="${actionMatrix.addRecord}">
        <el:modalLink
                link="${createLink(controller: 'aocListRecord', action: 'addRequestModal', params: [aocCorrespondenceListId: aocCorrespondenceList?.id])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                label="${message(code: 'aocListRecord.addRequest.label', args: [entity])}">
            <i class="ace-icon fa fa-plus"></i>
        </el:modalLink>
    </g:if>
    <g:if test="${actionMatrix.createRecord}">
        <el:modalLink
                link="${createLink(controller: 'aocListRecord', action: 'createRequestModal', params: [aocCorrespondenceListId: aocCorrespondenceList?.id])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-info"
                label="${message(code: 'aocListRecord.createRequest.label', args: [entity])}">
            <i class="ace-icon fa fa-plus-square"></i>
        </el:modalLink>
    </g:if>
    <g:if test="${aocCorrespondenceList?.correspondenceType == ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType.EVALUATION_LIST && actionMatrix.createRecord}">
        <el:modalLink
                link="${createLink(controller: 'aocEvaluationList', action: 'importEvaluationDataModal', params: [aocCorrespondenceListId: aocCorrespondenceList?.id])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-info"
                label="${message(code: 'aocCorrespondenceList.importEvaluationData.label', default: 'importEvaluationData')}">
            <i class="ace-icon fa fa-plus-square"></i>
        </el:modalLink>
    </g:if>
%{--to add allowanceRequest to list--}%
    <g:if test="${actionMatrix.startWorkflow}">

        <el:confirmLink
                msg="${message(code: 'aocCorrespondenceList.confirm.startWorkflow.label', default: 'workflow will be start, are you sure?')}"
                link="${createLink(controller: 'aocCorrespondenceList', action: 'startWorkflow', params: [encodedId: aocCorrespondenceList?.encodedId])}"
                preventCloseOutSide="true" class="btn btn-sm btn-danger"
                label="${message(code: 'workflow.start.label', default: 'start workflow')}">
            <i class="ace-icon fa fa-plus-square"></i>
        </el:confirmLink>
    </g:if>

%{--in case: list status CREATED, we add send button --}%
    <g:if test="${actionMatrix.sendList}">
        <el:confirmLink class="btn btn-sm btn-warning" label="${message(code: 'list.sendList.label')}"
                        msg="${message(code: 'list.sendList.label')}"
                        link='${createLink(controller: 'aocCorrespondenceList', action: 'sendList', params: [encodedId: aocCorrespondenceList?.encodedId])}'>
            <i class="ace-icon icon-paper-plane"></i>
        </el:confirmLink>
    </g:if>

    <g:if test="${actionMatrix.sendManagerialList}">
        <el:confirmLink class="btn btn-sm btn-danger" label="${message(code: 'list.sendManagerialList.label')}"
                        msg="${message(code: 'list.sendManagerialList.label')}"
                        link='${createLink(controller: 'aocCorrespondenceList', action: 'sendList', params: [encodedId: aocCorrespondenceList?.encodedId, isManagerial: true])}'>
            <i class="ace-icon icon-paper-plane"></i>
        </el:confirmLink>
    </g:if>

    <g:if test="${actionMatrix.setAsFinished}">
        <el:confirmLink class="btn btn-sm btn-danger"
                        label="${message(code: 'aocCorrespondenceList.setAsFinished.label')}"
                        msg="${message(code: 'aocCorrespondenceList.setAsFinished.label')}"
                        link='${createLink(controller: 'aocCorrespondenceList', action: 'finishList', params: [encodedId: aocCorrespondenceList?.encodedId])}'>
            <i class="ace-icon fa fa-close"></i>
        </el:confirmLink>
    </g:if>

    <g:if test="${actionMatrix.createRelatedOugoingList}">
        <btn:button messageCode="aocCorrespondenceList.createOutgoing.label" icon="fa fa-plus" color="default"
                    onClick="window.location.href='${createLink(controller: 'aocCorrespondenceList', action: 'createOutgoing',
                            params: [encodedId: aocCorrespondenceList?.encodedId, correspondenceType: aocCorrespondenceList.correspondenceType])}'"/>
    </g:if>

    <g:if test="${actionMatrix.createRelatedIncomingList}">
        <btn:button messageCode="aocCorrespondenceList.createIncoming.label" icon="fa fa-plus" color="default"
                    onClick="window.location.href='${createLink(controller: 'aocCorrespondenceList', action: 'createIncoming',
                            params: [encodedId: aocCorrespondenceList?.encodedId, correspondenceType: aocCorrespondenceList.correspondenceType])}'"/>
    </g:if>

    <btn:attachmentButton
            onClick="openAttachmentModal('${aocCorrespondenceList?.id}', '${aocCorrespondenceList?.threadId}')"
            accessUrl="${createLink(controller: 'attachment', action: 'filterAttachment')}"/>
</div>

<g:render template="/attachment/attachmentSharedTemplate" model="[
        referenceObject    : referenceObject,
        operationType      : operationType,
        sharedOperationType: sharedOperationType,
        attachmentTypeList : attachmentTypeList,
        isNonSharedObject  : true
]"/>

</body>
</html>
