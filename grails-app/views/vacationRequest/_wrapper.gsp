<lay:showWidget size="12" title="${message(code: 'employee.info.label')}">

    <lay:showWidget size="6">

        <lay:showElement value="${vacationRequest?.employee}" type="String"
                         label="${message(code: 'disciplinaryRequest.employee.label', default: 'personName')}"/>
        <lay:showElement value="${vacationRequest?.employee?.currentEmploymentRecord?.department?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

    </lay:showWidget>

    <lay:showWidget size="6">
        <lay:showElement value="${vacationRequest?.employee?.financialNumber}" type="String"
                         label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
        <lay:showElement value="${vacationRequest?.employee?.transientData?.governorateDTO?.descriptionInfo}"
                         type="String"
                         label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>

    </lay:showWidget>

</lay:showWidget>




<lay:showWidget size="12" title="${message(code: 'vacationRequest.label')}">

    <lay:showWidget size="6">

        <lay:showElement labelWidth="180" value="${vacationRequest?.vacationType?.descriptionInfo?.localName}"
                         type="VacationType"
                         label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"/>
        <lay:showElement value="${vacationRequest?.fromDate}" type="ZonedDate"
                         label="${message(code: 'vacationRequest.fromDate.label', default: 'fromDate')}"/>
    </lay:showWidget>

    <lay:showWidget size="6">
        <lay:showElement value="${vacationRequest?.numOfDays}" type="Integer"
                         label="${message(code: 'vacationRequest.numOfDays.label', default: 'numOfDays')}"/>

        <lay:showElement value="${vacationRequest?.returnDate}" type="ZonedDate"
                         label="${message(code: 'vacationRequest.returnDate.label', default: 'returnDate')}"/>
    </lay:showWidget>
</lay:showWidget>