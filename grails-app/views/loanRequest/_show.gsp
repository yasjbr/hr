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
<g:render template="/request/wrapperShow" model="[request:loanRequest]" />
<el:row />