<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${legalIdentifier?.documentOwner}" type="enum" label="${message(code:'legalIdentifier.documentOwner.label',default:'documentOwner')}" messagePrefix="RelatedParty" />
    <g:if test="${legalIdentifier?.documentOwner.equals( ps.police.pcore.enums.v1.RelatedParty.ORGANIZATION)}">
        <lay:showElement value="${legalIdentifier?.ownerOrganization}" type="Organization" label="${message(code:'legalIdentifier.ownerOrganization.label',default:'ownerOrganization')}" />
    </g:if>
    <g:if test="${legalIdentifier?.documentOwner.equals( ps.police.pcore.enums.v1.RelatedParty.PERSON)}">
        <lay:showElement value="${legalIdentifier?.ownerPerson}" type="Person" label="${message(code:'legalIdentifier.ownerPerson.label',default:'ownerPerson')}" />
    </g:if>
    <lay:showElement value="${legalIdentifier?.documentTypeClassification?.documentType}" type="String" label="${message(code:'legalIdentifier.documentType.label',default:'documentType')}" />
    <lay:showElement value="${legalIdentifier?.documentTypeClassification?.documentClassification}" type="String" label="${message(code:'legalIdentifier.documentClassification.label',default:'documentClassification')}" />
    <lay:showElement value="${legalIdentifier?.documentNumber}" type="String" label="${message(code:'legalIdentifier.documentNumber.label',default:'documentNumber')}" />

    <lay:showElement value="${legalIdentifier?.issuingDate}" type="ZonedDate" label="${message(code:'legalIdentifier.issuingDate.label',default:'issuingDate')}" />
    <lay:showElement value="${legalIdentifier?.validFrom}" type="ZonedDate" label="${message(code:'legalIdentifier.validFrom.label',default:'validFrom')}" />
    <lay:showElement value="${legalIdentifier?.validTo}" type="ZonedDate" label="${message(code:'legalIdentifier.validTo.label',default:'validTo')}" />
    <lay:showElement value="${legalIdentifier?.issuedByOrganization}" type="Organization" label="${message(code:'legalIdentifier.issuedByOrganization.label',default:'issuedByOrganization')}" />

    <lay:showElement value="${legalIdentifier?.legalIdentifierLevel}" type="LegalIdentifierLevel" label="${message(code:'legalIdentifier.legalIdentifierLevel.label',default:'legalIdentifierLevel')}" />
    <lay:showElement value="${legalIdentifier?.countryOfOrigin}" type="Country" label="${message(code:'legalIdentifier.countryOfOrigin.label',default:'countryOfOrigin')}" />
    <lay:showElement value="${legalIdentifier?.note}" type="String" label="${message(code:'legalIdentifier.note.label',default:'note')}" />
    <lay:showElement value="${legalIdentifier?.legalIdentifierRelatedRestrictions?.toString()?.replace("]","")?.replace("[","")}" type="Set" label="${message(code:'legalIdentifier.legalIdentifierRelatedRestrictions.label',default:'legalIdentifierRelatedRestrictions')}" />


    <lay:showElement value="${legalIdentifier?.issueLocation?(legalIdentifier?.issueLocation?.toString() + "${legalIdentifier?.unstructuredIssueLocation?(" - "+legalIdentifier?.unstructuredIssueLocation):""}"):""}" type="String" label="${message(code:'legalIdentifier.issueLocation.label',default:'issueLocation')}" />


</lay:showWidget>