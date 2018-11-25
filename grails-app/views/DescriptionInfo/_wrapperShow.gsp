
    <lay:showElement
                  label="${messageValue?:(message(code:'descriptionInfo.localName.label',default:'localName'))}"
                  value="${bean?.localName}" />

    <lay:showElement
                   label="${message(code:'descriptionInfo.latinName.label',default:'latinName')}"
                   value="${bean?.latinName}"/>

    <lay:showElement
                   label="${message(code:'descriptionInfo.hebrewName.label',default:'hebrewName')}"
                   value="${bean?.hebrewName}"/>