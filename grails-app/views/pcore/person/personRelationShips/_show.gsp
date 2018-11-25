<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personRelationShips?.person}" type="Person" label="${message(code:'personRelationShips.person.label',default:'person')}" />
    <lay:showElement value="${personRelationShips?.relatedPerson}" type="Person" label="${message(code:'personRelationShips.relatedPerson.label',default:'relatedPerson')}" />
    <lay:showElement value="${personRelationShips?.relationshipType}" type="RelationshipType" label="${message(code:'personRelationShips.relationshipType.label',default:'relationshipType')}" />
    <lay:showElement value="${personRelationShips?.fromDate}" type="ZonedDate" label="${message(code:'personRelationShips.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${personRelationShips?.toDate}" type="ZonedDate" label="${message(code:'personRelationShips.toDate.label',default:'toDate')}" />
    <lay:showElement value="${personRelationShips?.isDependent}" type="Boolean" label="${message(code:'personRelationShips.isDependent.label',default:'isDependent')}" />
</lay:showWidget>