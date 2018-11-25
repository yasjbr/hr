<% def firstRequest = allowanceRequestList[0] %>

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

<g:each in="${allowanceRequestList}" var="allowanceRequest">
    <lay:showWidget size="12" title="${message(code: 'request.info.label')}">

        <g:render template="/request/wrapperRequestShow" model="[request: allowanceRequest]"/>

        <lay:showWidget size="6">
            <lay:showElement value="${allowanceRequest?.allowanceType?.descriptionInfo?.localName}" type="string"
                             label="${message(code: 'allowanceRequest.allowanceType.label', default: 'requestType')}"
                             messagePrefix="EnumRequestType"/>
            <lay:showElement value="${allowanceRequest?.requestReason}" type="String"
                             label="${message(code: 'request.requestReason.label', default: 'requestReason')}"/>
        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${allowanceRequest?.effectiveDate}" type="ZonedDate"
                             label="${message(code: 'allowanceRequest.effectiveDate.label', default: 'effectiveDate')}"
                             messagePrefix="effectiveDate"/>
            <lay:showElement value="${allowanceRequest?.toDate}" type="ZonedDate"
                             label="${message(code: 'allowanceRequest.toDate.label', default: 'toDate')}"
                             messagePrefix="toDate"/>
        </lay:showWidget>

        <lay:showWidget size="6">
            <g:if test="${allowanceRequest?.personRelationShipsId}">

                <lay:showElement value="${firstRequest?.transientData?.personRelationShipsName}" type="string"
                                 label="${message(code: 'allowanceRequest.personRelationShipsId.label', default: 'personRelationShipsId')}"
                                 messagePrefix="personRelationShipsId"/>
            </g:if>
            <lay:showElement value="${allowanceRequest?.requestStatusNote}" type="String"
                             label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>
        </lay:showWidget>
    </lay:showWidget>
</g:each>
