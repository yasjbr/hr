<g:render template="/DescriptionInfo/wrapper" model="[bean: allowanceType?.descriptionInfo]"/>


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8"
                     class=" " controller="relationshipType"
                     action="autocomplete" name="relationshipTypeId"
                     label="${message(code: 'allowanceType.relationshipTypeId.label', default: 'relationshipTypeId')}"
                     values="${[[allowanceType?.relationshipTypeId,
                                 allowanceType?.transientData?.relationshipTypeName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'allowanceType.universalCode.label', default: 'universalCode')}"
                  value="${allowanceType?.universalCode}"/>
</el:formGroup>