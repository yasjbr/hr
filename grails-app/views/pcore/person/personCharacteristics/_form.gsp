<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                             isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personCharacteristics?.person,
                                             isDisabled:isPersonDisabled]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="hairFeature" action="autocomplete" name="hairFeature.id" label="${message(code:'personCharacteristics.hairFeature.label',default:'hairFeature')}" values="${[[personCharacteristics?.hairFeature?.id,personCharacteristics?.hairFeature?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:textField name="personVoice" size="8"  class="" label="${message(code:'personCharacteristics.personVoice.label',default:'personVoice')}" value="${personCharacteristics?.personVoice}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" paramsGenerateFunction="skinColorParam" class=" " controller="color" action="autocomplete" name="skinColor.id" label="${message(code:'personCharacteristics.skinColor.label',default:'skinColor')}" values="${[[personCharacteristics?.skinColor?.id,personCharacteristics?.skinColor?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" paramsGenerateFunction="eyeColorParam" class=" " controller="color" action="autocomplete" name="eyeColor.id" label="${message(code:'personCharacteristics.eyeColor.label',default:'eyeColor')}" values="${[[personCharacteristics?.eyeColor?.id,personCharacteristics?.eyeColor?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" paramsGenerateFunction="hairColorParam" class=" " controller="color" action="autocomplete" name="hairColor.id" label="${message(code:'personCharacteristics.hairColor.label',default:'hairColor')}" values="${[[personCharacteristics?.hairColor?.id,personCharacteristics?.hairColor?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="informationMarker" size="8"  class="" label="${message(code:'personCharacteristics.informationMarker.label',default:'informationMarker')}" value="${personCharacteristics?.informationMarker}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="specialSkills" size="8"  class="" label="${message(code:'personCharacteristics.specialSkills.label',default:'specialSkills')}" value="${personCharacteristics?.specialSkills}"/>
</el:formGroup>

<script type="text/javascript">

    function skinColorParam() {
        return {'colorCategory.id': '${ps.police.pcore.enums.v1.ColorCategory.SKIN_COLORS.value()}'}
    }

    function eyeColorParam() {
        return {'colorCategory.id': '${ps.police.pcore.enums.v1.ColorCategory.COLORS_EYES.value()}'}
    }

    function hairColorParam() {
        return {'colorCategory.id': '${ps.police.pcore.enums.v1.ColorCategory.HAIR_COLORS.value()}'}
    }


</script>