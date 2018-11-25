<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${profileNote?.employee}" type="Employee" label="${message(code:'profileNote.employee.label',default:'employee')}" />
    <lay:showElement value="${profileNote?.note}" type="String" label="${message(code:'profileNote.note.label',default:'note')}" />
    <lay:showElement value="${profileNote?.noteDate}" type="ZonedDate" label="${message(code:'profileNote.noteDate.label',default:'noteDate')}" />
    <lay:showElement value="${profileNote?.orderNo}" type="String" label="${message(code:'profileNote.orderNo.label',default:'orderNo')}" />
</lay:showWidget>