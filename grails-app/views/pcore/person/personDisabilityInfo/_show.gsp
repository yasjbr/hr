<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personDisabilityInfo?.person}" type="Person" label="${message(code:'personDisabilityInfo.person.label',default:'person')}" />
    <lay:showElement value="${personDisabilityInfo?.disabilityType}" type="DisabilityType" label="${message(code:'personDisabilityInfo.disabilityType.label',default:'disabilityType')}" />
    <lay:showElement value="${personDisabilityInfo?.disabilityLevel}" type="DisabilityLevel" label="${message(code:'personDisabilityInfo.disabilityLevel.label',default:'disabilityLevel')}" />
    <lay:showElement value="${formatNumber(number: personDisabilityInfo?.percentage,maxFractionDigits: 0)}%" type="String" label="${message(code:'personDisabilityInfo.percentage.label',default:'percentage')}" />
    <lay:showElement value="${personDisabilityInfo?.accommodationNeeded}" type="boolean" label="${message(code:'personDisabilityInfo.accommodationNeeded.label',default:'accommodationNeeded')}" />

</lay:showWidget>