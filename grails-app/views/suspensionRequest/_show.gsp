<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${suspensionRequest?.fromDate}" type="ZonedDate" label="${message(code:'suspensionRequest.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${suspensionRequest?.toDate}" type="ZonedDate" label="${message(code:'suspensionRequest.toDate.label',default:'toDate')}" />
    <lay:showElement value="${suspensionRequest?.periodInMonth}" type="Short" label="${message(code:'suspensionRequest.periodInMonth.label',default:'periodInMonth')}" />
    <lay:showElement value="${suspensionRequest?.suspensionType}" type="enum" label="${message(code:'suspensionRequest.suspensionType.label',default:'suspensionType')}" messagePrefix="EnumSuspensionType" />
</lay:showWidget>
