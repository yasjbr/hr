
<g:if test="${!params.isEmployeeDisabled}">
    <lay:showWidget size="12" title="${message(code: 'employee.info.label')}">

        <lay:showWidget size="6">

            <lay:showElement value="${bordersSecurityCoordination?.employee}" type="String"
                             label="${message(code: 'disciplinaryRequest.employee.label', default: 'personName')}"/>
            <lay:showElement value="${bordersSecurityCoordination?.employee?.currentEmploymentRecord?.department?.descriptionInfo}"
                             type="String"
                             label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${bordersSecurityCoordination?.employee?.financialNumber}" type="String"
                             label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
            <lay:showElement value="${bordersSecurityCoordination?.employee?.transientData?.governorateDTO?.descriptionInfo}"
                             type="String"
                             label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>

        </lay:showWidget>

    </lay:showWidget>
</g:if>



<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <g:render template="/request/wrapperRequestShow" model="[request: bordersSecurityCoordination]"/>

    <lay:showElement value="${bordersSecurityCoordination?.requestDate}" type="ZonedDate"
                     label="${message(code: 'bordersSecurityCoordination.requestDate.label', default: 'requestDate')}"/>
    <lay:showElement value="${bordersSecurityCoordination?.fromDate}" type="ZonedDate"
                     label="${message(code: 'bordersSecurityCoordination.fromDate.label', default: 'fromDate')}"/>
    <lay:showElement value="${bordersSecurityCoordination?.toDate}" type="ZonedDate"
                     label="${message(code: 'bordersSecurityCoordination.toDate.label', default: 'toDate')}"/>
    <lay:showElement value="${bordersSecurityCoordination?.transientData?.documentTypeDTO?.descriptionInfo?.localName}"
                     type="string"
                     label="${message(code: 'bordersSecurityCoordination.legalIdentifierId.label', default: 'legalIdentifierId')}"/>
    <lay:showElement
            value="${bordersSecurityCoordination?.transientData?.borderCrossingPointDTO?.descriptionInfo?.localName}"
            type="string"
            label="${message(code: 'bordersSecurityCoordination.borderLocationId.label', default: 'borderLocationId')}"/>
    <lay:showElement value="${bordersSecurityCoordination?.requestReason}" type="String"
                     label="${message(code: 'bordersSecurityCoordination.requestReason.label', default: 'requestReason')}"/>
    <lay:showElement value="${bordersSecurityCoordination?.unstructuredLocation}" type="String"
                     label="${message(code: 'bordersSecurityCoordination.unstructuredLocation.label', default: 'unstructuredLocation')}"/>

</lay:showWidget>


<g:render template="/request/wrapperShow" model="[request: bordersSecurityCoordination]"/>

<el:row/>

<g:if test="${!params.isEmployeeDisabled}">
    <div class="clearfix form-actions text-center">

        <g:if test="${bordersSecurityCoordination?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">
            <btn:editButton
                    onClick="window.location.href='${createLink(controller: 'bordersSecurityCoordination', action: 'edit', params: [encodedId: "${bordersSecurityCoordination?.encodedId}"])}'"/>
    </g:if>
        <btn:backButton goToPreviousLink="true" withPreviousLink="true"/>
    </div>
</g:if>