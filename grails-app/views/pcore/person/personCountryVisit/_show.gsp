<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personCountryVisit?.reasonForVisit}" type="String" label="${message(code:'personCountryVisit.reasonForVisit.label',default:'reasonForVisit')}" />
    <lay:showElement value="${personCountryVisit?.startVisitDate}" type="ZonedDate" label="${message(code:'personCountryVisit.startVisitDate.label',default:'startVisitDate')}" />
    <lay:showElement value="${personCountryVisit?.endVisitDate}" type="ZonedDate" label="${message(code:'personCountryVisit.endVisitDate.label',default:'endVisitDate')}" />
    <lay:showElement value="${personCountryVisit?.location?(personCountryVisit?.location?.toString() + "${personCountryVisit?.unstructuredLocation?(" - "+personCountryVisit?.unstructuredLocation):""}"):""}" type="String" label="${message(code:'personCountryVisit.location.label',default:'location')}" />
</lay:showWidget>
