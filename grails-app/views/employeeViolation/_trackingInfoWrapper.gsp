<lay:showWidget size="12" title="${message(code: 'employeeViolation.trackingInfo.info.label')}">
    <lay:showElement value="${violation?.trackingInfo?.dateCreatedUTC}"
                     type="ZonedDate"
                     label="${message(code: 'employeeViolation.dateCreatedUTC.label', default: 'dateCreatedUTC')}"/>
    <lay:showElement value="${violation?.trackingInfo?.createdBy}"
                     type="String"
                     label="${message(code: 'employeeViolation.createdBy.label', default: 'createdBy')}"/>
    <lay:showElement value="${violation?.trackingInfo?.lastUpdatedUTC}"
                     type="ZonedDate"
                     label="${message(code: 'employeeViolation.lastUpdatedUTC.label', default: 'lastUpdatedUTC')}"/>
    <lay:showElement value="${violation?.trackingInfo?.lastUpdatedBy}"
                     type="String"
                     label="${message(code: 'employeeViolation.lastUpdatedBy.label', default: 'lastUpdatedBy')}"/>
</lay:showWidget>