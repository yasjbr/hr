<g:render template="/pcore/person/wrapper" model="[bean:personLanguageInfo?.person,isSearch:true]" />

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="language" action="autocomplete" name="language.id" label="${message(code:'personLanguageInfo.language.label',default:'language')}" values="${[[personLanguageInfo?.language?.id,personLanguageInfo?.language?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="educationLevel" action="autocomplete" name="readingLevel.id" label="${message(code:'personLanguageInfo.readingLevel.label',default:'readingLevel')}" values="${[[personLanguageInfo?.readingLevel?.id,personLanguageInfo?.readingLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="educationLevel" action="autocomplete" name="verbalLevel.id" label="${message(code:'personLanguageInfo.verbalLevel.label',default:'verbalLevel')}" values="${[[personLanguageInfo?.verbalLevel?.id,personLanguageInfo?.verbalLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="educationLevel" action="autocomplete" name="writingLevel.id" label="${message(code:'personLanguageInfo.writingLevel.label',default:'writingLevel')}" values="${[[personLanguageInfo?.writingLevel?.id,personLanguageInfo?.writingLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="isMother" size="8"  class=" " label="${message(code:'personLanguageInfo.isMother.label',default:'isMother')}" value="${personLanguageInfo?.isMother}" isChecked="${personLanguageInfo?.isMother}" />
</el:formGroup>