<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'suspensionExtensionRequest.entity', default: 'SuspensionExtensionRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'SuspensionExtensionRequest List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'suspensionExtensionRequest', action: 'list')}'"/>
    </div></div>
</div>


<lay:showWidget size="12" title="${message(code: 'employee.info.label')}">

    <lay:showWidget size="6">

        <lay:showElement value="${suspensionExtensionRequest?.suspensionRequest?.employee}" type="String"
                         label="${message(code: 'disciplinaryRequest.employee.label', default: 'personName')}"/>
        <lay:showElement
                value="${suspensionExtensionRequest?.suspensionRequest?.employee?.currentEmploymentRecord?.department?.descriptionInfo}"
                type="String"
                label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

    </lay:showWidget>

    <lay:showWidget size="6">
        <lay:showElement value="${suspensionExtensionRequest?.suspensionRequest?.employee?.financialNumber}"
                         type="String"
                         label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
        <lay:showElement
                value="${suspensionExtensionRequest?.suspensionRequest?.employee?.transientData?.governorateDTO?.descriptionInfo}"
                type="String"
                label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>

    </lay:showWidget>

</lay:showWidget>


<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <g:render template="/request/wrapperRequestShow" model="[request: suspensionExtensionRequest]"/>




    <lay:showElement value="${suspensionExtensionRequest?.employee}" type="Employee"
                     label="${message(code: 'suspensionExtensionRequest.employee.label', default: 'employee')}"/>
    <lay:showElement value="${suspensionExtensionRequest?.fromDate}" type="ZonedDate"
                     label="${message(code: 'suspensionExtensionRequest.fromDate.label', default: 'fromDate')}"/>
    <lay:showElement value="${suspensionExtensionRequest?.toDate}" type="ZonedDate"
                     label="${message(code: 'suspensionExtensionRequest.toDate.label', default: 'toDate')}"/>
    <lay:showElement value="${suspensionExtensionRequest?.periodInMonth}" type="Short"
                     label="${message(code: 'suspensionExtensionRequest.periodInMonth.label', default: 'numOfDays')}"/>
    <lay:showElement value="${suspensionExtensionRequest?.requestReason}" type="String"
                     label="${message(code: 'suspensionExtensionRequest.requestReason.label', default: 'requestReason')}"/>
    <lay:showElement value="${suspensionExtensionRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'suspensionExtensionRequest.requestStatusNote.label', default: 'requestStatusNote')}"/>




</lay:showWidget>


<g:render template="/request/wrapperShow" model="[request: suspensionExtensionRequest]"/>

<el:row/>

<g:if test="${suspensionExtensionRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">

    <div class="clearfix form-actions text-center">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'suspensionExtensionRequest', action: 'edit', params: [encodedId: "${suspensionExtensionRequest?.encodedId}"])}'"/>
    </div>

</g:if>
</body>
</html>