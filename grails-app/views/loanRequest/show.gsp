<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'loanRequest.entity', default: 'LoanRequest List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'LoanRequest List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'loanRequest',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${message(code: 'request.info.label')}">


    <g:render template="/request/wrapperRequestShow" model="[request:loanRequest]" />
    <lay:showElement value="${loanRequest?.requestedJob}" type="String" label="${message(code:'loanRequest.requestedJob.label',default:'requestedJob')}" />
    <lay:showElement value="${loanRequest?.requestedJobTitle}" type="String" label="${message(code:'loanRequest.requestedJobTitle.label',default:'requestedJobTitle')}" />
    <lay:showElement value="${loanRequest?.fromDate}" type="ZonedDate" label="${message(code:'loanRequest.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${loanRequest?.toDate}" type="ZonedDate" label="${message(code:'loanRequest.toDate.label',default:'toDate')}" />
    <lay:showElement value="${loanRequest?.periodInMonths}" type="Short" label="${message(code:'loanRequest.periodInMonths.label',default:'periodInMonths')}" />
    <lay:showElement value="${loanRequest?.toDepartment}" type="Department" label="${message(code:'loanRequest.toDepartment.label',default:'toDepartment')}" />
    <lay:showElement value="${loanRequest?.numberOfPositions}" type="Short" label="${message(code:'loanRequest.numberOfPositions.label',default:'numberOfPositions')}" />
    <lay:showElement value="${loanRequest?.transientData?.requestedFromOrganizationDTO}" type="String" label="${message(code:'loanRequest.requestedFromOrganizationId.label',default:'requestedFromOrganizationId')}" />

    <lay:showElement value="${loanRequest?.loanRequestRelatedPersons?.findAll{it.recordSource == ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource.REQUESTED}?.transientData?.requestedPersonDTO?.join(",")}" type="String" label="${message(code:'loanRequest.loanRequestRelatedPerson.label',default:'loanRequestRelatedPersons')}" />
    <lay:showElement value="${loanRequest?.loanRequestRelatedPersons?.findAll{it.recordSource == ps.gov.epsilon.hr.enums.loan.v1.EnumPersonSource.RECEIVED}?.transientData?.requestedPersonDTO?.join(",")}" type="String" label="${message(code:'loanRequest.loanRequestReceivedPerson.label',default:'loanRequestRelatedPersons')}" />





    <lay:showElement value="${loanRequest?.requestReason}" type="String"
                     label="${message(code:'request.requestReason.label',default:'requestReason')}" />


    <lay:showElement value="${loanRequest?.description}" type="String"
                     label="${message(code:'loanRequest.description.label',default:'description')}" />

</lay:showWidget>
<el:row />
<g:render template="/request/wrapperManagerialOrderShow" model="[request: loanRequest, colSize: 12]"/>
<el:row />
<g:render template="/request/wrapperShow" model="[request:loanRequest]" />
<el:row />


<div class="clearfix form-actions text-center">
    <g:if test="${loanRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'loanRequest', action: 'edit', params: [encodedId: loanRequest?.encodedId])}'"/>
    </g:if>
    <g:if test="${loanRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || loanRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || loanRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || loanRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
        <btn:button messageCode="loanList.entities" color="pink"
                    icon="fa fa-bars" size="bigger" class="width-135"
                    onClick="window.location.href='${createLink(controller: 'loanRequest', action: 'goToList',
                            params: [encodedId: loanRequest?.encodedId])}'"/>
    </g:if>
    <btn:backButton />
</div>

</body>
</html>