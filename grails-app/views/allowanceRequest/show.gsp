<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'allowanceRequest.entity', default: 'AllowanceRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'AllowanceRequest List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'allowanceRequest', action: 'list')}'"/>
    </div></div>
</div>
<el:row/>
<el:row/>

<lay:showWidget size="12" title="${message(code: 'employee.info.label')}">

    <lay:showWidget size="6">
        <lay:showElement value="${allowanceRequest?.employee}" type="String"
                         label="${message(code: 'disciplinaryRequest.employee.label', default: 'personName')}"/>
        <lay:showElement value="${allowanceRequest?.employee?.currentEmploymentRecord?.department?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

    </lay:showWidget>

    <lay:showWidget size="6">
        <lay:showElement value="${allowanceRequest?.employee?.financialNumber}" type="String"
                         label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
        <lay:showElement value="${allowanceRequest?.employee?.transientData?.governorateDTO?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>

    </lay:showWidget>

</lay:showWidget>



<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <g:render template="/request/wrapperRequestShow" model="[request: allowanceRequest, colSize: 8]"/>



    <lay:showElement value="${allowanceRequest?.allowanceType?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'allowanceRequest.allowanceType.label', default: 'requestType')}"
                     messagePrefix="EnumRequestType"/>
    <lay:showElement value="${allowanceRequest?.effectiveDate}" type="ZonedDate"
                     label="${message(code: 'allowanceRequest.effectiveDate.label', default: 'effectiveDate')}"
                     messagePrefix="effectiveDate"/>

    <lay:showElement value="${allowanceRequest?.toDate}" type="ZonedDate"
                     label="${message(code: 'allowanceRequest.toDate.label', default: 'toDate')}"
                     messagePrefix="toDate"/>


    <g:if test="${allowanceRequest?.personRelationShipsId}">

        <lay:showElement value="${allowanceRequest?.transientData?.personRelationShipsName}" type="string"
                         label="${message(code: 'allowanceRequest.personRelationShipsId.label', default: 'personRelationShipsId')}"
                         messagePrefix="personRelationShipsId"/>
    </g:if>

    <lay:showElement value="${allowanceRequest?.requestReason}" type="String"
                     label="${message(code: 'request.requestReason.label', default: 'requestReason')}"/>
    <lay:showElement value="${allowanceRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>

</lay:showWidget>

<g:render template="/request/wrapperManagerialOrderShow" model="[request: allowanceRequest, colSize: 12]"/>
<g:render template="/request/wrapperShow" model="[request: allowanceRequest]"/>





%{--the bellow rows to add space btw show widget--}%
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>
<el:row class="col-sm-6" size="6"/> <br/>

<el:row/>
<div class="clearfix form-actions text-center">
    <g:if test="${allowanceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'allowanceRequest', action: 'edit', params: [encodedId: allowanceRequest?.encodedId])}'"/>
    </g:if>

    <g:if test="${allowanceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || allowanceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || allowanceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || allowanceRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="allowanceList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'allowanceRequest', action: 'goToList',
                            params: [encodedId: allowanceRequest?.encodedId])}'"/>
    </g:if>

    <el:formButton functionName="back" goToPreviousLink="true"/>
</div>

</body>
</html>