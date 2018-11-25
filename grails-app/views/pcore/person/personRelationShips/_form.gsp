
<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personRelationShips?.person,
                                             isDisabled:isPersonDisabled]" />

<g:render template="/pcore/person/wrapper" model="[id:'relatedPersonId',
                                             name:'relatedPerson.id',
                                             bean:personRelationShips?.relatedPerson,
                                             messageValue:message(code: 'personRelationShips.relatedPerson.label')
                                             ]" />

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="isRequired" controller="relationshipType" action="autocomplete" name="relationshipType.id" label="${message(code:'personRelationShips.relationshipType.label',default:'relationshipType')}" values="${[[personRelationShips?.relationshipType?.id,personRelationShips?.relationshipType?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'personRelationShips.fromDate.label',default:'fromDate')}" value="${personRelationShips?.fromDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'personRelationShips.toDate.label',default:'toDate')}" value="${personRelationShips?.toDate}" />
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="isDependent" size="8"  class=" " label="${message(code:'personRelationShips.isDependent.label',default:'isDependent')}" value="${personRelationShips?.isDependent}" isChecked="${personRelationShips?.isDependent}" />
</el:formGroup>


