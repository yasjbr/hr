<g:if test="${!params.isEmployeeDisabled}">
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
</g:if>


<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <g:render template="/request/wrapperRequestShow" model="[request: vacationRequest]"/>

    <lay:showElement labelWidth="180" value="${vacationRequest?.vacationType?.descriptionInfo?.localName}"
                     type="VacationType"
                     label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"/>
    <lay:showElement value="${vacationRequest?.fromDate}" type="ZonedDate"
                     label="${message(code: 'vacationRequest.fromDate.label', default: 'fromDate')}"/>
    <lay:showElement value="${vacationRequest?.toDate}" type="ZonedDate"
                     label="${message(code: 'vacationRequest.toDate.label', default: 'toDate')}"/>
    <lay:showElement value="${vacationRequest?.returnDate}" type="ZonedDate"
                     label="${message(code: 'vacationRequest.returnDate.label', default: 'returnDate')}"/>
    <lay:showElement value="${vacationRequest?.numOfDays}" type="Integer"
                     label="${message(code: 'vacationRequest.numOfDays.label', default: 'numOfDays')}"/>
    <lay:showElement value="${vacationRequest?.currentBalance > 0 ? vacationRequest?.currentBalance : '0'}"
                     type="Double"
                     label="${message(code: 'vacationRequest.currentBalance.label', default: 'currentBalance')}"/>
    <lay:showElement value="${vacationRequest?.external}" type="Boolean"
                     label="${message(code: 'vacationRequest.external.label', default: 'external')}"/>
    <g:if test="${vacationRequest?.external == true}">
        <lay:showElement
                value="${vacationRequest?.securityCoordination?.transientData?.borderCrossingPointDTO?.descriptionInfo?.localName}"
                type="BordersSecurityCoordination"
                label="${message(code: 'vacationRequest.securityCoordination.label', default: 'securityCoordination')}"/>

    </g:if>

    <lay:showElement value="${vacationRequest?.requestReason}" type="String"
                     label="${message(code: 'vacationRequest.requestReason.label', default: 'requestReason')}"/>

    <g:if test="${vacationRequest?.isStopped == true}">
        <lay:showElement value="${vacationRequest?.isStopped}" type="Boolean"
                         label="${message(code: 'vacationRequest.isStopped.label', default: 'isStopped')}"/>

        <lay:showElement value="${vacationRequest?.stoppedBy}" type="Employee"
                         label="${message(code: 'vacationRequest.stoppedBy.label', default: 'stoppedBy')}"/>

    </g:if>

</lay:showWidget>


<g:render template="/request/wrapperManagerialOrderShow" model="[request: vacationRequest, colSize: 12]"/>
<g:render template="/request/wrapperShow" model="[request: vacationRequest]"/>

<el:row/>

<g:if test="${!params.isEmployeeDisabled && !hide}">
    <div class="clearfix form-actions text-center">
        <g:if test="${vacationRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.CREATED}">

            <btn:editButton
                    onClick="window.location.href='${createLink(controller: 'vacationRequest', action: 'edit', params: [encodedId: vacationRequest?.encodedId])}'"/>
        </g:if>


        <g:if test="${vacationRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.ADD_TO_LIST || vacationRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.SENT_BY_LIST || vacationRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED || vacationRequest?.requestStatus == ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.REJECTED}">
            <btn:button messageCode="vacationList.entities" color="pink"
                        icon="fa fa-bars" size="bigger" class="width-135"
                        onClick="window.location.href='${createLink(controller: 'vacationRequest', action: 'goToList',
                                params: [encodedId: vacationRequest?.encodedId])}'"/>
        </g:if>


        <el:formButton functionName="back" goToPreviousLink="true"/>
    </div>
</g:if>