<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personEmploymentHistory?.person}" type="Person" label="${message(code:'personEmploymentHistory.person.label',default:'person')}" />
    <lay:showElement value="${personEmploymentHistory?.professionType}" type="ProfessionType" label="${message(code:'personEmploymentHistory.professionType.label',default:'professionType')}" />

    <lay:showElement value="${personEmploymentHistory?.jobDescription}" type="String" label="${message(code:'personEmploymentHistory.jobDescription.label',default:'jobDescription')}" />

    <g:if test="${personEmploymentHistory?.organization}">
        <lay:showElement value="${personEmploymentHistory?.organization}" type="Organization" label="${message(code:'personEmploymentHistory.organization.label',default:'organization')}" />

    </g:if>
    <g:else>
        <lay:showElement value="${personEmploymentHistory?.organizationName}" type="String" label="${message(code:'personEmploymentHistory.organizationName.label',default:'organizationName')}" />
    </g:else>
    <lay:showElement value="${personEmploymentHistory?.fromDate}" type="ZonedDate" label="${message(code:'personEmploymentHistory.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${personEmploymentHistory?.toDate}" type="ZonedDate" label="${message(code:'personEmploymentHistory.toDate.label',default:'toDate')}" />

    <lay:showElement value="${personEmploymentHistory?.location?(personEmploymentHistory?.location?.toString() + "${personEmploymentHistory?.unstructuredLocation?(" - "+personEmploymentHistory?.unstructuredLocation):""}"):""}" type="String" label="${message(code:'personEmploymentHistory.location.label',default:'location')}" />

</lay:showWidget>