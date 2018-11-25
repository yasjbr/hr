<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeeViolation?.employee}" type="employee" label="${message(code:'employeeViolation.employee.label',default:'employee')}" />


    <lay:showElement value="${employeeViolation?.disciplinaryReason}" type="DisciplinaryReason" label="${message(code:'employeeViolation.disciplinaryReason.label',default:'disciplinaryReason')}" />

    <lay:showElement value="${employeeViolation?.violationDate}" type="ZonedDate" label="${message(code:'employeeViolation.violationDate.label',default:'violationDate')}" />

    <lay:showElement value="${employeeViolation?.informer}" type="Employee" label="${message(code:'employeeViolation.informer.label',default:'informer')}" />

    <lay:showElement value="${employeeViolation?.noticeDate}" type="ZonedDate" label="${message(code:'employeeViolation.noticeDate.label',default:'noticeDate')}" />

    <lay:showElement value="${employeeViolation?.violationStatus}" type="enum" label="${message(code:'employeeViolation.violationStatus.label',default:'violationStatus')}" messagePrefix="EnumViolationStatus" />

    <lay:showElement value="${employeeViolation?.unstructuredLocation?(employeeViolation?.transientData?.locationDTO?.toString() + " - " + employeeViolation?.unstructuredLocation):(employeeViolation?.transientData?.locationDTO)}" type="String" label="${message(code:'employeeViolation.locationId.label',default:'locationId')}" />

    <lay:showElement value="${employeeViolation?.note}" type="String" label="${message(code:'employeeViolation.note.label',default:'note')}" />


</lay:showWidget>
<el:row />