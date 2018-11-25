<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personHealthHistory?.person}" type="Person" label="${message(code:'personHealthHistory.person.label',default:'person')}" />
    <lay:showElement value="${personHealthHistory?.diseaseType}" type="DiseaseType" label="${message(code:'personHealthHistory.diseaseType.label',default:'diseaseType')}" />
    <lay:showElement value="${personHealthHistory?.diseaseName}" type="String" label="${message(code:'personHealthHistory.diseaseName.label',default:'diseaseName')}" />
    <lay:showElement value="${personHealthHistory?.description}" type="String" label="${message(code:'personHealthHistory.description.label',default:'description')}" />
    <lay:showElement value="${personHealthHistory?.affictionDate}" type="ZonedDate" label="${message(code:'personHealthHistory.affictionDate.label',default:'affictionDate')}" />
    <lay:showElement value="${personHealthHistory?.affictionLocation?(personHealthHistory?.affictionLocation?.toString() + "${personHealthHistory?.unstructuredAffictionLocation?(" - "+personHealthHistory?.unstructuredAffictionLocation):""}"):""}" type="String" label="${message(code:'personHealthHistory.affictionLocation.label',default:'affictionLocation')}" />
</lay:showWidget>