
<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personNationality?.person,
                                             isDisabled:isPersonDisabled]" />

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="country" action="autocomplete" name="granterCountry.id" label="${message(code:'personNationality.granterCountry.label',default:'granterCountry')}" values="${[[personNationality?.granterCountry?.id,personNationality?.granterCountry?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="nationalityAcquisitionMethod" action="autocomplete"
                     name="acquisitionMethod.id" label="${message(code:'personNationality.acquisitionMethod.label',default:'acquisitionMethod')}" values="${[[personNationality?.acquisitionMethod?.id,personNationality?.acquisitionMethod?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="acquiredDate"  size="8" class=" " label="${message(code:'personNationality.acquiredDate.label',default:'acquiredDate')}" value="${personNationality?.acquiredDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="expiryDate"  size="8" class=" "
                  label="${message(code:'personNationality.expiryDate.label',default:'expiryDate')}"
                  value="${personNationality?.expiryDate}" />
</el:formGroup>
