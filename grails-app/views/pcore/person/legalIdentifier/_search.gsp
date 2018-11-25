<el:formGroup>
    <el:textField name="documentNumber" size="8"  class=" " label="${message(code:'legalIdentifier.documentNumber.label',default:'documentNumber')}" value="${legalIdentifier?.documentNumber}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="issuingDate"  size="8" class=" " label="${message(code:'legalIdentifier.issuingDate.label',default:'issuingDate')}" value="${legalIdentifier?.issuingDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="validFrom"  size="8" class=" " label="${message(code:'legalIdentifier.validFrom.label',default:'validFrom')}" value="${legalIdentifier?.validFrom}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="validTo"  size="8" class=" " label="${message(code:'legalIdentifier.validTo.label',default:'validTo')}" value="${legalIdentifier?.validTo}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="organization" action="autocomplete" name="issuedByOrganization.id" label="${message(code:'legalIdentifier.issuedByOrganization.label',default:'issuedByOrganization')}" values="${[[legalIdentifier?.issuedByOrganization?.id,legalIdentifier?.issuedByOrganization?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="RelatedParty" from="${ps.police.pcore.enums.v1.RelatedParty.values()}" name="documentOwner" size="8"  class=" " label="${message(code:'legalIdentifier.documentOwner.label',default:'documentOwner')}" value="${legalIdentifier?.documentOwner}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organization" action="autocomplete" name="ownerOrganization.id" label="${message(code:'legalIdentifier.ownerOrganization.label',default:'ownerOrganization')}" values="${[[legalIdentifier?.ownerOrganization?.id,legalIdentifier?.ownerOrganization?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="person" action="autocomplete" name="ownerPerson.id" label="${message(code:'legalIdentifier.ownerPerson.label',default:'ownerPerson')}" values="${[[legalIdentifier?.ownerPerson?.id,legalIdentifier?.ownerPerson?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="legalIdentifierLevel" action="autocomplete" name="legalIdentifierLevel.id" label="${message(code:'legalIdentifier.legalIdentifierLevel.label',default:'legalIdentifierLevel')}" values="${[[legalIdentifier?.legalIdentifierLevel?.id,legalIdentifier?.legalIdentifierLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="country" action="autocomplete" name="countryOfOrigin.id" label="${message(code:'legalIdentifier.countryOfOrigin.label',default:'countryOfOrigin')}" values="${[[legalIdentifier?.countryOfOrigin?.id,legalIdentifier?.countryOfOrigin?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'legalIdentifier.note.label',default:'note')}" value="${legalIdentifier?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" "
                     controller="documentClassification" action="autocomplete" name="documentClassification.id"
                     label="${message(code:'legalIdentifier.documentClassification.label',default:'documentClassification')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" "
                     controller="documentType" action="autocomplete"
                     name="documentType.id"
                     label="${message(code:'legalIdentifier.documentType.label',
                             default:'documentType')}"  />
</el:formGroup>
<g:render template="/pcore/location/searchWrapper" />
<el:formGroup>
    <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" />
</el:formGroup>