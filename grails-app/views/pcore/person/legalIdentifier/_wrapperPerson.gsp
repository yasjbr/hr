<g:set var="classValue" value="${isRequired?"isRequired":""}" />
<g:set var="fieldName" value="${fieldName?:"legalIdentifier"}" />


<el:hiddenField name="${fieldName}.id" value="${legalIdentifier?.id}"/>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" ${classValue}"
                     controller="documentType" action="autocomplete"
                     name="documentType.id" id="documentTypeId"
                     label="${message(code:'legalIdentifier.documentType.label',
                             default:'documentType')}"
                     values="${[[legalIdentifier?.documentTypeClassification?.documentType?.id,
                                 legalIdentifier?.documentTypeClassification?.documentType?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" ${classValue}"  paramsGenerateFunction="documentTypeParams"
                     controller="documentClassification" action="autocomplete" name="documentClassification.id" id="documentClassificationId"
                     label="${message(code:'legalIdentifier.documentClassification.label',default:'documentClassification')}"
                     values="${[[legalIdentifier?.documentTypeClassification?.documentClassification?.id,
                                 legalIdentifier?.documentTypeClassification?.documentClassification?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="${fieldName}.documentNumber" size="8"  class=" ${classValue}" label="${message(code:'legalIdentifier.documentNumber.label',default:'documentNumber')}" value="${legalIdentifier?.documentNumber}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="${fieldName}.validFrom"  size="8" class=" ${classValue}" label="${message(code:'legalIdentifier.validFrom.label',default:'validFrom')}" value="${legalIdentifier?.validFrom}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="${fieldName}.validTo"  size="8" class=" ${classValue}" label="${message(code:'legalIdentifier.validTo.label',default:'validTo')}" value="${legalIdentifier?.validTo}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" ${classValue}" controller="organization"
                     action="autocomplete" name="${fieldName}.issuedByOrganization.id"
                     label="${message(code:'legalIdentifier.issuedByOrganization.label',default:'issuedByOrganization')}"
                     values="${[[legalIdentifier?.issuedByOrganization?.id,
                                 legalIdentifier?.issuedByOrganization?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=""
                     controller="legalIdentifierLevel" action="autocomplete" name="${fieldName}.legalIdentifierLevel.id"
                     label="${message(code:'legalIdentifier.legalIdentifierLevel.label',default:'legalIdentifierLevel')}"
                     values="${[[legalIdentifier?.legalIdentifierLevel?.id,
                                 legalIdentifier?.legalIdentifierLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=""
                     controller="country" action="autocomplete" name="${fieldName}.countryOfOrigin.id"
                     label="${message(code:'legalIdentifier.countryOfOrigin.label',default:'countryOfOrigin')}"
                     values="${[[legalIdentifier?.countryOfOrigin?.id,
                                 legalIdentifier?.countryOfOrigin?.descriptionInfo?.localName]]}" />
</el:formGroup>

<lay:wall title="${g.message(code: 'legalIdentifier.issueLocation.label')}">
    <g:render template="/pcore/location/wrapper" model="[location:legalIdentifier?.issueLocation,fieldName:'legalIdentifier.location',isRequired:false]" />
    <el:formGroup>
        <el:textArea name="${fieldName}.unstructuredIssueLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" value="${legalIdentifier?.unstructuredIssueLocation}"/>
    </el:formGroup>
</lay:wall>

<el:formGroup>
    <el:textField name="${fieldName}.note" size="8"  class="" label="${message(code:'legalIdentifier.note.label',default:'note')}" value="${legalIdentifier?.note}"/>
</el:formGroup>


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" multiple="multiple" class=""
                     controller="legalIdentifierRestriction" action="autocomplete"
                     name="${fieldName}.legalIdentifierRelatedRestrictions.id"
                     label="${message(code:'legalIdentifier.legalIdentifierRelatedRestrictions.label',
                             default:'legalIdentifierRelatedRestrictions')}"
                     values="${legalIdentifier?.legalIdentifierRelatedRestrictions?.legalIdentifierRestriction?.collect{return [it?.id,it?.descriptionInfo?.localName]}}" />
</el:formGroup>



<el:hiddenField name="${fieldName}.ownerPerson.id" value="${legalIdentifier?.ownerPerson?.id}"/>
<el:hiddenField name="${fieldName}.documentOwner" value="${ps.police.pcore.enums.v1.RelatedParty.PERSON}"/>

