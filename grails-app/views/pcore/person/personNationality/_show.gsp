<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personNationality?.person}" type="Person" label="${message(code:'personNationality.person.label',default:'person')}" />
    <lay:showElement value="${personNationality?.granterCountry}" type="Country" label="${message(code:'personNationality.granterCountry.label',default:'granterCountry')}" />
    <lay:showElement value="${personNationality?.acquisitionMethod}" type="NationalityAcquisitionMethod" label="${message(code:'personNationality.acquisitionMethod.label',default:'acquisitionMethod')}" />

    <lay:showElement value="${personNationality?.acquiredDate}" type="ZonedDate" label="${message(code:'personNationality.acquiredDate.label',default:'acquiredDate')}" />
    <lay:showElement value="${personNationality?.expiryDate}" type="ZonedDate" label="${message(code:'personNationality.expiryDate.label',default:'expiryDate')}" />

</lay:showWidget>
