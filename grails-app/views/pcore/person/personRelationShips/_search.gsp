<g:render template="/pcore/person/wrapper" model="[bean:personRelationShips?.person,isSearch:true]" />
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" " label="${message(code:'personRelationShips.fromDate.label',default:'fromDate')}" value="${personRelationShips?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="isDependent" size="8"  class=" " label="${message(code:'personRelationShips.isDependent.label',default:'isDependent')}" value="${personRelationShips?.isDependent}" isChecked="${personRelationShips?.isDependent}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="person" action="autocomplete" name="relatedPerson.id" label="${message(code:'personRelationShips.relatedPerson.label',default:'relatedPerson')}" values="${[[personRelationShips?.relatedPerson?.id,personRelationShips?.relatedPerson?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="relationshipDate"  size="8" class=" " label="${message(code:'personRelationShips.relationshipDate.label',default:'relationshipDate')}" value="${personRelationShips?.relationshipDate}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="relationshipType" action="autocomplete" name="relationshipType.id" label="${message(code:'personRelationShips.relationshipType.label',default:'relationshipType')}" values="${[[personRelationShips?.relationshipType?.id,personRelationShips?.relationshipType?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" " label="${message(code:'personRelationShips.toDate.label',default:'toDate')}" value="${personRelationShips?.toDate}" />
</el:formGroup>
