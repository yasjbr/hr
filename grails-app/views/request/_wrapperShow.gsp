<lay:showWidget size="12" title="${message(code: 'request.trackingInfo.info.label')}">
    <lay:showElement value="${request?.trackingInfo?.dateCreatedUTC}"
                     type="ZonedDate"
                     label="${message(code: 'request.dateCreatedUTC.label', default: 'dateCreatedUTC')}"/>
    <lay:showElement value="${request?.trackingInfo?.createdBy}"
                     type="String"
                     label="${message(code: 'request.createdBy.label', default: 'createdBy')}"/>
    <lay:showElement value="${request?.trackingInfo?.lastUpdatedUTC}"
                     type="ZonedDate"
                     label="${message(code: 'request.lastUpdatedUTC.label', default: 'lastUpdatedUTC')}"/>
    <lay:showElement value="${request?.trackingInfo?.lastUpdatedBy}"
                     type="String"
                     label="${message(code: 'request.lastUpdatedBy.label', default: 'lastUpdatedBy')}"/>
    <g:if test="${viewDetails}">
        <lay:showElement value="${request?.requestReason}"
                         type="String"
                         label="${message(code: 'request.requestReason.label', default: 'requestReason')}"/>
        <lay:showElement value="${request?.requestStatusNote}"
                         type="String"
                         label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>
    </g:if>
</lay:showWidget>