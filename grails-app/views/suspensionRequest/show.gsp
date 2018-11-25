<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'suspensionRequest.entity', default: 'EmployeePromotion List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'EmployeePromotion List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'suspensionRequest', action: 'list')}'"/>
    </div></div>
</div>

<lay:showWidget size="12" title="${message(code: 'employee.info.label')}">

    <lay:showWidget size="6">

        <lay:showElement value="${suspensionRequest?.employee}" type="String"
                         label="${message(code: 'disciplinaryRequest.employee.label', default: 'personName')}"/>
        <lay:showElement value="${suspensionRequest?.employee?.currentEmploymentRecord?.department?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

    </lay:showWidget>

    <lay:showWidget size="6">
        <lay:showElement value="${suspensionRequest?.employee?.financialNumber}" type="String"
                         label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
        <lay:showElement value="${suspensionRequest?.employee?.transientData?.governorateDTO?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>

    </lay:showWidget>

</lay:showWidget>



<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <g:render template="/request/wrapperRequestShow" model="[request: suspensionRequest]"/>

    <lay:showElement labelWidth="180" value="${suspensionRequest?.suspensionType}"
                     type="Enum"
                     label="${message(code: 'suspensionRequest.suspensionType.label', default: 'suspensionType')}"/>
    <lay:showElement value="${suspensionRequest?.fromDate}" type="ZonedDate"
                     label="${message(code: 'suspensionRequest.fromDate.label', default: 'fromDate')}"/>
    <lay:showElement value="${suspensionRequest?.toDate}" type="ZonedDate"
                     label="${message(code: 'suspensionRequest.toDate.label', default: 'toDate')}"/>

    <lay:showElement value="${suspensionRequest?.periodInMonth}" type="short"
                     label="${message(code: 'suspensionRequest.periodInMonth.label', default: 'periodInMonth')}"/>



    <lay:showElement value="${suspensionRequest?.requestReason}" type="String"
                     label="${message(code: 'suspensionRequest.requestReason.label', default: 'requestReason')}"/>



</lay:showWidget>

<g:render template="/request/wrapperManagerialOrderShow" model="[request: suspensionRequest, colSize: 12]"/>

<g:render template="/request/wrapperShow" model="[request: suspensionRequest]"/>

<el:row/>


<div class="clearfix form-actions text-center">
    <g:if test="${suspensionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">

        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'suspensionRequest', action: 'edit', params: [encodedId: suspensionRequest?.encodedId])}'"/>
    </g:if>


    <g:if test="${suspensionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || suspensionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || suspensionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || suspensionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="suspensionList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'suspensionRequest', action: 'goToList',
                            params: [encodedId: suspensionRequest?.encodedId])}'"/>
    </g:if>

    <el:formButton functionName="back" goToPreviousLink="true"/>
</div>

</body>
</html>