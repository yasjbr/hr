<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personLiveStatus?.person}" type="Person" label="${message(code:'personLiveStatus.person.label',default:'person')}" />
    <lay:showElement value="${personLiveStatus?.liveStatus}" type="LiveStatus" label="${message(code:'personLiveStatus.liveStatus.label',default:'liveStatus')}" />

    <lay:showElement value="${personLiveStatus?.fromDate}" type="ZonedDate" label="${message(code:'personLiveStatus.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${personLiveStatus?.toDate}" type="ZonedDate" label="${message(code:'personLiveStatus.toDate.label',default:'toDate')}" />
    <lay:showElement value="${personLiveStatus?.note}" type="String" label="${message(code:'personLiveStatus.note.label',default:'note')}" />
</lay:showWidget>