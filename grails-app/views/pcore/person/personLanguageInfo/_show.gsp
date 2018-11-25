<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${personLanguageInfo?.person}" type="Person" label="${message(code:'personLanguageInfo.person.label',default:'person')}" />
    <lay:showElement value="${personLanguageInfo?.language}" type="Language" label="${message(code:'personLanguageInfo.language.label',default:'language')}" />
    <lay:showElement value="${personLanguageInfo?.readingLevel}" type="EducationLevel" label="${message(code:'personLanguageInfo.readingLevel.label',default:'readingLevel')}" />
    <lay:showElement value="${personLanguageInfo?.verbalLevel}" type="EducationLevel" label="${message(code:'personLanguageInfo.verbalLevel.label',default:'verbalLevel')}" />
    <lay:showElement value="${personLanguageInfo?.writingLevel}" type="EducationLevel" label="${message(code:'personLanguageInfo.writingLevel.label',default:'writingLevel')}" />
    <lay:showElement value="${personLanguageInfo?.isMother}" type="Boolean" label="${message(code:'personLanguageInfo.isMother.label',default:'isMother')}" />
</lay:showWidget>