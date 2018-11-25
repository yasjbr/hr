<g:set var="messagePrefix" value="${params?.justAddress == "true" ? "contactInfoAddress":"contactInfo"}" />
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${contactInfo?.relatedObjectType}" type="enum" label="${message(code:messagePrefix+'.relatedObjectType.label',default:'relatedObjectType')}" messagePrefix="ContactInfoClassification" />

    <g:if test="${contactInfo?.relatedObjectType == ps.police.pcore.enums.v1.ContactInfoClassification.ORGANIZATION}">
        <lay:showElement value="${contactInfo?.organization}" type="Organization" label="${message(code:messagePrefix+'.organization.label',default:'organization')}" />
    </g:if>
    <g:if test="${contactInfo?.relatedObjectType== ps.police.pcore.enums.v1.ContactInfoClassification.PERSON}">
        <lay:showElement value="${contactInfo?.person}" type="Person" label="${message(code:messagePrefix+'.person.label',default:'ownerPerson')}" />
    </g:if>
    <lay:showElement value="${contactInfo?.contactType}" type="ContactType" label="${message(code:messagePrefix+'.contactType.label',default:'contactType')}" />
    <lay:showElement value="${contactInfo?.contactMethod}" type="ContactMethod" label="${message(code:messagePrefix+'.contactMethod.label',default:'contactMethod')}" />

    <g:if test="${messagePrefix == "contactInfoAddress"}">
        <lay:showElement value="${contactInfo?.value?(contactInfo?.address?.toString() + "-" + contactInfo?.value):(contactInfo?.address?.toString())}" type="String" label="${message(code:messagePrefix+'.value.label',default:'value')}" />
    </g:if>
    <g:else>
        <lay:showElement value="${contactInfo?.value}" type="String" label="${message(code:'contactInfo.contactInfoDescription.label',default:'description')}" />
    </g:else>
    %{--<lay:showElement value="${contactInfo?.fromDate}" type="ZonedDate" label="${message(code:'contactInfo.fromDate.label',default:'fromDate')}" />--}%
    %{--<lay:showElement value="${contactInfo?.toDate}" type="ZonedDate" label="${message(code:'contactInfo.toDate.label',default:'toDate')}" />--}%
</lay:showWidget>