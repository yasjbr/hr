<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personCharacteristics?.person}" type="Person" label="${message(code:'personCharacteristics.person.label',default:'person')}" />
    <lay:showElement value="${personCharacteristics?.eyeColor}" type="Color" label="${message(code:'personCharacteristics.eyeColor.label',default:'eyeColor')}" />
    <lay:showElement value="${personCharacteristics?.hairColor}" type="Color" label="${message(code:'personCharacteristics.hairColor.label',default:'hairColor')}" />
    <lay:showElement value="${personCharacteristics?.hairFeature}" type="HairFeature" label="${message(code:'personCharacteristics.hairFeature.label',default:'hairFeature')}" />
    <lay:showElement value="${personCharacteristics?.informationMarker}" type="String" label="${message(code:'personCharacteristics.informationMarker.label',default:'informationMarker')}" />
    <lay:showElement value="${personCharacteristics?.personVoice}" type="String" label="${message(code:'personCharacteristics.personVoice.label',default:'personVoice')}" />
    <lay:showElement value="${personCharacteristics?.skinColor}" type="Color" label="${message(code:'personCharacteristics.skinColor.label',default:'skinColor')}" />
    <lay:showElement value="${personCharacteristics?.specialSkills}" type="String" label="${message(code:'personCharacteristics.specialSkills.label',default:'specialSkills')}" />
</lay:showWidget>