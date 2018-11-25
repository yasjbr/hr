<% def firstRequest = vacationRequestList[0] %>

<lay:showWidget size="12" title="${message(code: 'employee.info.label')}">

    <lay:showWidget size="6">
        <lay:showElement value="${firstRequest?.employee}" type="String"
                         label="${message(code: 'disciplinaryRequest.employee.label', default: 'personName')}"/>
        <lay:showElement value="${firstRequest?.employee?.currentEmploymentRecord?.department?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

    </lay:showWidget>

    <lay:showWidget size="6">
        <lay:showElement value="${firstRequest?.employee?.financialNumber}" type="String"
                         label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
        <lay:showElement value="${firstRequest?.employee?.transientData?.governorateDTO?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>

    </lay:showWidget>

</lay:showWidget>

<g:each in="${vacationRequestList}" var="vacationRequest">
    <lay:showWidget size="12" title="${message(code: 'request.info.label')}">

        <g:render template="/request/wrapperRequestShow" model="[request: vacationRequest]"/>

        <lay:showWidget size="6">
            <lay:showElement value="${vacationRequest?.vacationType?.descriptionInfo?.localName}" type="string"
                             label="${message(code: 'vacationRequest.vacationType.label', default: 'requestType')}"
                             messagePrefix="EnumRequestType"/>
            <lay:showElement value="${vacationRequest?.requestReason}" type="String"
                             label="${message(code: 'request.requestReason.label', default: 'requestReason')}"/>
        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${vacationRequest?.effectiveDate}" type="ZonedDate"
                             label="${message(code: 'vacationRequest.effectiveDate.label', default: 'effectiveDate')}"
                             messagePrefix="effectiveDate"/>
            <lay:showElement value="${vacationRequest?.toDate}" type="ZonedDate"
                             label="${message(code: 'vacationRequest.toDate.label', default: 'toDate')}"
                             messagePrefix="toDate"/>
        </lay:showWidget>


    </lay:showWidget>
</g:each>
