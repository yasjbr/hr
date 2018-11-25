<lay:showWidget size="12" title="${message(code: 'request.info.label')}">

    <lay:showElement value="${allowanceRequest?.id}" type="long"
                     label="${message(code: 'allowanceRequest.allowanceRequestId.label', default: 'allowanceRequestId')}"/>
    <lay:showElement value="${allowanceRequest?.requestDate}" type="ZonedDate"
                     label="${message(code: 'request.requestDate.label', default: 'requestDate')}"/>
    <lay:showElement value="${allowanceRequest?.requestStatus}" type="enum"
                     label="${message(code: 'allowanceRequest.requestStatus.label', default: 'requestStatus')}"
                     messagePrefix="EnumRequestStatus"/>

    <lay:showElement value="${allowanceRequest?.allowanceType?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'allowanceRequest.allowanceType.label', default: 'requestType')}"
                     messagePrefix="EnumRequestType"/>
    <lay:showElement value="${allowanceRequest?.effectiveDate}" type="ZonedDate"
                     label="${message(code: 'allowanceRequest.effectiveDate.label', default: 'requestStatus')}"
                     messagePrefix="EnumRequestStatus"/>

    <lay:showElement value="${allowanceRequest?.requestReason}" type="String"
                     label="${message(code: 'request.requestReason.label', default: 'requestReason')}"/>
    <lay:showElement value="${allowanceRequest?.requestStatusNote}" type="String"
                     label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>

</lay:showWidget>

<el:row/>