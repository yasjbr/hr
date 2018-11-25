<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personMaritalStatus?.person}" type="Person" label="${message(code:'personMaritalStatus.person.label',default:'person')}" />
    <lay:showElement value="${personMaritalStatus?.maritalStatus}" type="String" label="${message(code:'personMaritalStatus.maritalStatus.label',default:'personMaritalStatus')}" />
    <lay:showElement value="${personMaritalStatus?.fromDate}" type="ZonedDate" label="${message(code:'personMaritalStatus.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${personMaritalStatus?.toDate}" type="ZonedDate" label="${message(code:'personMaritalStatus.toDate.label',default:'toDate')}" />
    <lay:showElement value="${personMaritalStatus?.isCurrent}" type="Boolean" label="${message(code:'personMaritalStatus.isCurrent.label',default:'isCurrent')}" />
    <lay:showElement value="${personMaritalStatus?.note}" type="String" label="${message(code:'personMaritalStatus.note.label',default:'note')}" />
</lay:showWidget>